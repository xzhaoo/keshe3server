package com.keshe3.keshe3server.service;

import com.keshe3.keshe3server.entity.Task;
import com.keshe3.keshe3server.enums.ETaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.concurrent.TimeUnit;

@Service
public class PythonScriptService {

    @Autowired
    private ITaskService taskService;

    /**
     * 执行python脚本
     * @param file
     * @param task
     */
    public void executeScript(MultipartFile file, Task task) {
        Process process = null;
        try {
            // 构建命令
            String[] command = {"python", "/path/to/your/script.py"};

            // 启动进程
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            process = processBuilder.start();

            // 获取进程的输入、输出和错误流
            OutputStream stdin = process.getOutputStream();
            InputStream stdout = process.getInputStream();
            InputStream stderr = process.getErrorStream();

            // 启动线程读取标准输出（结果）
            ByteArrayOutputStream resultBuffer = new ByteArrayOutputStream();
            Thread stdoutThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = stdout.read(buffer)) != -1) {
                        resultBuffer.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            stdoutThread.start();

            // 启动线程读取标准错误（进度和状态信息）
            Thread stderrThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(stderr))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.equals("COMPLETED")) {
                            // 任务完成
                            taskService.updateTaskStatus(task.getId(), ETaskStatus.COMPLETED);
                        } else if (line.startsWith("ERROR:")) {
                            // 任务失败
                            taskService.updateTaskStatus(task.getId(), ETaskStatus.FAILED);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            stderrThread.start();

            // 将文件内容写入进程的标准输入
            stdin.write(file.getBytes());
            stdin.close(); // 关闭输入流，表示输入结束

            // 等待进程完成
            boolean finished = process.waitFor(30, TimeUnit.MINUTES); // 设置超时时间
            if (!finished) {
                process.destroy();
                taskService.updateTaskStatus(task.getId(), ETaskStatus.FAILED);
                return;
            }

            // 等待线程完成
            stdoutThread.join();
            stderrThread.join();

            // 检查退出码
            int exitCode = process.exitValue();
            if (exitCode != 0 && !ETaskStatus.FAILED.getCode().equals(task.getTaskStatus())) {
                taskService.updateTaskStatus(task.getId(), ETaskStatus.FAILED);
            } else if (ETaskStatus.COMPLETED.getCode().equals(task.getTaskStatus())) {
                // 保存处理结果
                taskService.updateEndTime(task.getId());
            }

        } catch (IOException | InterruptedException e) {
            taskService.updateTaskStatus(task.getId(), ETaskStatus.FAILED);
            Thread.currentThread().interrupt();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}