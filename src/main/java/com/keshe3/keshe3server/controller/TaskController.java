package com.keshe3.keshe3server.controller;

import com.keshe3.keshe3server.entity.Task;
import com.keshe3.keshe3server.enums.EMediaType;
import com.keshe3.keshe3server.enums.ETaskStatus;
import com.keshe3.keshe3server.resp.PythonDetectionResp;
import com.keshe3.keshe3server.resp.TzResp;
import com.keshe3.keshe3server.service.IMediaService;
import com.keshe3.keshe3server.service.ITaskService;
import com.keshe3.keshe3server.service.TaskQueueService;
import com.keshe3.keshe3server.service.impl.TaskProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.messaging.simp.SimpMessagingTemplate; // 导入模板类
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import com.keshe3.keshe3server.entity.Media; // 导入Media实体
import java.util.Map; // 导入Map
import java.util.HashMap; // 导入HashMap

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
    private RestTemplate restTemplate;

    @Value("${python.server-path}")
    private String pythonServerPath;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private TaskProcessingService taskProcessingService;

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
            String userId = (String) request.getAttribute("userId");

            String contentType = file.getContentType();

            EMediaType mediaType = EMediaType.IMAGE;
            if (contentType != null && contentType.startsWith("video/")) {
                mediaType = EMediaType.VIDEO;
            }

            String mediaId = mediaService.buildMediaInfo(userId, mediaType.getCode(), file.getOriginalFilename(), file.getSize());
            if (mediaId == null) {
                System.out.println("上传文件失败");
                return TzResp.fail(500, "上传文件失败");
            }

            String taskId = taskService.buildTaskInfo(userId, mediaId);
            if (taskId == null) {
                System.out.println("创建任务失败");
                return TzResp.fail(500, "创建任务失败");
            }

            // 保存文件内容到字节数组
            byte[] fileContent = file.getBytes();

            // 获取原始文件名，用于通知
            String originalFilename = file.getOriginalFilename();

            // 派发任务
            taskQueueService.addTask(() -> {
                taskProcessingService.processTask(userId, taskId, mediaId, originalFilename, fileContent);
            });

            // 创建一个Map存放要返回的数据
            Map<String, Object> responseData = new HashMap<>();

            // 将mediaId放入 Map 中
            responseData.put("mediaId", mediaId);

            // 将这个Map作为成功响应的数据返回
            return TzResp.success(responseData);

        } catch (Exception e) {
            System.out.println("添加任务失败"+e.getMessage());
            return TzResp.fail(500, "添加任务失败: " + e.getMessage());
        }
    }

}