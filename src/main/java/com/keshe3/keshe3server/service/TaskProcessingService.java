package com.keshe3.keshe3server.service;

import com.keshe3.keshe3server.enums.ETaskStatus;
import com.keshe3.keshe3server.resp.PythonDetectionResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
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

    /**
     * 处理任务
     * @param userId
     * @param taskId
     * @param mediaId
     * @param originalFilename
     * @param fileContent
     */
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
                    pythonServerPath,
                    HttpMethod.POST,
                    requestEntity,
                    PythonDetectionResp.class
            );

            PythonDetectionResp respBody = response.getBody();

            if (response.getStatusCode() == HttpStatus.OK && respBody != null && (respBody.getCode() == 200 || respBody.getCode() == 499)) {
                mediaService.setMediaPath(mediaId, respBody.getAbsoluteUrl());
                taskService.updateEndTime(taskId);
                taskService.updateTaskStatus(taskId, ETaskStatus.COMPLETED);

                sendTaskUpdate(userId, taskId, mediaId, originalFilename, "COMPLETED");
                System.out.println("任务成功/部分完成：" + respBody.getMessage());
            } else {
                taskService.updateTaskStatus(taskId, ETaskStatus.FAILED);
                sendTaskUpdate(userId, taskId, mediaId, originalFilename, "FAILED");
                System.out.println("任务失败：" + (respBody != null ? respBody.getMessage() : "HTTP status not OK"));
            }

        } catch (Exception e) {
            System.out.println("任务执行异常 error");
            taskService.updateTaskStatus(taskId, ETaskStatus.FAILED);
            sendTaskUpdate(userId, taskId, mediaId, originalFilename, "FAILED");
            e.printStackTrace();
        }
    }

    /**
     * 发送任务更新消息
     * @param userId
     * @param taskId
     * @param mediaId
     * @param mediaName
     * @param status
     */
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