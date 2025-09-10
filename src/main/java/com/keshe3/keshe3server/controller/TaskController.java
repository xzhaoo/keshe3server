package com.keshe3.keshe3server.controller;

import com.keshe3.keshe3server.entity.Task;
import com.keshe3.keshe3server.enums.EMediaType;
import com.keshe3.keshe3server.enums.ETaskStatus;
import com.keshe3.keshe3server.resp.TzResp;
import com.keshe3.keshe3server.service.IMediaService;
import com.keshe3.keshe3server.service.ITaskService;
import com.keshe3.keshe3server.service.PythonScriptService;
import com.keshe3.keshe3server.service.TaskQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("task")
public class TaskController {

    @Autowired
    private ITaskService taskService;

    @Autowired
    private IMediaService mediaService;

    @Autowired
    private TaskQueueService taskQueueService;

    @Autowired
    private PythonScriptService pythonScriptService;

    /**
     * 添加任务到队列
     * @param file
     * @param request
     * @return TzResp
     */
    @PostMapping("addTask")
    public TzResp<?> addTask(@RequestParam("file") MultipartFile file,
                             HttpServletRequest request) {
        try {
            //传回前端信息为待处理
            // 获取用户ID
            String userId = (String) request.getAttribute("userId");

            // 判断文件类型（视频为1，图片为0）
            String contentType = file.getContentType();
            EMediaType mediaType = EMediaType.valueOf("01"); // 默认为图片
            if (contentType != null && contentType.startsWith("video/")) {
                mediaType = EMediaType.valueOf("02"); // 视频
            }

            // 获取并保存文件（调用上传文件）（媒体服务）
            String mediaId = mediaService.buildMediaInfo(userId, mediaType.getCode(), file.getOriginalFilename(), file.getSize());
            if (mediaId == null) {
                return TzResp.fail(500, "上传文件失败");
            }

            // 创建任务信息并保存至数据库（调用信息创建）
            Task task = taskService.buildTaskInfo(userId, mediaId);
            if (task == null) {
                return TzResp.fail(500, "创建任务失败");
            }

            // 将任务添加到队列中异步执行
            taskQueueService.addTask(() -> {
                try {
                    taskService.updateTaskStatus(task.getId(), ETaskStatus.PROCESSING);
                    // 执行python脚本
                    pythonScriptService.executeScript(file, task);
                    taskService.updateTaskStatus(task.getId(), ETaskStatus.COMPLETED);
                } catch (Exception e) {
                    // 处理任务执行异常
                    e.printStackTrace();
                }
            });

            return TzResp.success("任务已添加到队列");
        } catch (Exception e) {
            return TzResp.fail(500, "添加任务失败: " + e.getMessage());
        }
    }
}