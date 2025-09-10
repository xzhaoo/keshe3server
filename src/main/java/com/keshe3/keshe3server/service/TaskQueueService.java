package com.keshe3.keshe3server.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class TaskQueueService {

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(5); // 调整线程数根据需要

    private volatile boolean running = true;

    /**
     * 初始化方法，在组件实例化后自动执行
     * 使用@PostConstruct注解标记，确保在依赖注入完成后调用
     * 该方法主要启动一个后台线程用于任务处理
     */
    @PostConstruct
    public void init() {
        // 启动任务处理线程
        // 使用Lambda表达式this::processTasks创建线程任务
        // 该线程将执行processTasks方法，用于处理后台任务
        new Thread(this::processTasks).start();
    }

    /**
     * 处理任务的核心方法，持续从任务队列中获取任务并执行
     * 当running状态为false时，方法会退出循环
     */
    private void processTasks() {
        while (running) {
            try {
                Runnable task = taskQueue.take(); // 阻塞直到有任务可用
                executorService.execute(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // 处理异常但不终止循环
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加任务到任务队列的方法
     * @param task 要执行的任务，实现了Runnable接口
     */
    public void addTask(Runnable task) {
        try {
            // 将任务放入任务队列，如果队列满则阻塞
            taskQueue.put(task);
        } catch (InterruptedException e) {
            // 捕获中断异常，并恢复中断状态
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 关闭方法，用于停止线程池的运行
     * 该方法会将运行状态设置为false，并关闭执行器服务
     */
    public void shutdown() {
        running = false;
        executorService.shutdown();
    }

    /**
     * 获取任务队列的大小
     *
     * @return 返回当前任务队列中的元素数量
     */
    public int getQueueSize() {
        return taskQueue.size();
    }
}