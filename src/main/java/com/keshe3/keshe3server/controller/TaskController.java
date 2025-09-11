package com.keshe3.keshe3server.controller;

import com.keshe3.keshe3server.entity.Task;
import com.keshe3.keshe3server.enums.EMediaType;
import com.keshe3.keshe3server.enums.ETaskStatus;
import com.keshe3.keshe3server.resp.TzResp;
import com.keshe3.keshe3server.service.IMediaService;
import com.keshe3.keshe3server.service.ITaskService;
import com.keshe3.keshe3server.service.TaskQueueService;
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

            taskQueueService.addTask(() -> {
                try {
                    taskService.updateTaskStatus(taskId, ETaskStatus.PROCESSING);

                    // 构建请求头
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                    // 使用ByteArrayResource在内存中处理文件
                    ByteArrayResource fileResource = new ByteArrayResource(fileContent) {
                        @Override
                        public String getFilename() {
                            return file.getOriginalFilename();
                        }
                    };

                    // 构建请求体
                    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                    body.add("file", fileResource);

                    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                    // 发送POST请求
                    ResponseEntity<String> response = restTemplate.exchange(
                            pythonServerPath,
                            HttpMethod.POST,
                            requestEntity,
                            String.class
                    );

                    // 检查响应状态
                    if (response.getStatusCode() == HttpStatus.OK) {
                        String resultUrl = response.getBody();
                        // 保存处理后的链接到任务结果中
//                        taskService.updateTaskResult(task.getId(), resultUrl);
                        mediaService.setMediaPath(mediaId, resultUrl);
                        taskService.updateEndTime(taskId);
                        taskService.updateTaskStatus(taskId, ETaskStatus.COMPLETED);
                    } else {
                        System.out.println("任务失败");
                        taskService.updateTaskStatus(taskId, ETaskStatus.FAILED);
                    }
                } catch (Exception e) {
                    System.out.println("任务失败error");
                    taskService.updateTaskStatus(taskId, ETaskStatus.FAILED);
                    e.printStackTrace();
                }
            });

            return TzResp.success("任务已添加到队列");
        } catch (Exception e) {
            System.out.println("添加任务失败"+e.getMessage());
            return TzResp.fail(500, "添加任务失败: " + e.getMessage());
        }
    }
}