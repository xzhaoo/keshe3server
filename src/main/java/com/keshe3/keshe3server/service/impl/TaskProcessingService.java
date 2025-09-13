// lhz
// 2025.9.13 15:02
package com.keshe3.keshe3server.service.impl;

import com.keshe3.keshe3server.enums.ETaskStatus;
import com.keshe3.keshe3server.resp.PythonDetectionResp;
import com.keshe3.keshe3server.service.IMediaService;
import com.keshe3.keshe3server.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TaskProcessingService {

    @Autowired
    private ITaskService taskService;

    @Autowired
    private IMediaService mediaService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Value("${python.server-path}")
    private String pythonServerPath;

    // 核心方法：给这个方法加上全新的事务
    @Transactional
    public void processTask(String userId, String taskId, String mediaId, String originalFilename, byte[] fileContent) {
        try {
            taskService.updateTaskStatus(taskId, ETaskStatus.PROCESSING);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource fileResource = new ByteArrayResource(fileContent) {
                @Override
                public String getFilename() {
                    return originalFilename;
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<PythonDetectionResp> response = restTemplate.exchange(
                    pythonServerPath + "/detect/",
                    HttpMethod.POST,
                    requestEntity,
                    PythonDetectionResp.class
            );

            PythonDetectionResp respBody = response.getBody();
            System.out.println(response);

            if (response.getStatusCode() == HttpStatus.OK && respBody != null && (respBody.getCode() == 200 || respBody.getCode() == 499)) {
                mediaService.setMediaPath(mediaId, respBody.getAbsoluteUrl());
                taskService.updateEndTime(taskId);
                taskService.updateTaskStatus(taskId, ETaskStatus.COMPLETED);

                // 可以安全地使用，因为在新的事务内部
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        sendTaskUpdate(userId, taskId, mediaId, originalFilename, "COMPLETED");
                    }
                });
                System.out.println("任务成功/部分完成：" + respBody.getMessage());
            } else {
                taskService.updateTaskStatus(taskId, ETaskStatus.FAILED);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        sendTaskUpdate(userId, taskId, mediaId, originalFilename, "FAILED");
                    }
                });
                System.out.println("任务失败：" + (respBody != null ? respBody.getMessage() : "HTTP status not OK"));
            }

        } catch (Exception e) {
            System.out.println("任务执行异常 error");
            taskService.updateTaskStatus(taskId, ETaskStatus.FAILED);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    sendTaskUpdate(userId, taskId, mediaId, originalFilename, "FAILED");
                }
            });
            e.printStackTrace();
            // 注意: @Transactional 默认在发生未检查异常时回滚，这里我们手动更新状态，所以可能需要更精细的异常处理
            // 但对于当前场景，这个逻辑是OK的。
        }
    }

    private void sendTaskUpdate(String userId, String taskId, String mediaId, String mediaName, String status) {
        Map<String, String> payload = new HashMap<>();
        payload.put("taskId", taskId);
        payload.put("mediaId", mediaId);
        payload.put("mediaName", mediaName);
        payload.put("taskStatus", status);

        String destination = "/topic/tasks/" + userId;
        messagingTemplate.convertAndSend(destination, payload);
        System.out.println("WebSocket message sent to " + destination + ": " + payload);
    }
}