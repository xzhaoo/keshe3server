package com.keshe3.keshe3server.controller;

import com.keshe3.keshe3server.entity.Media;
import com.keshe3.keshe3server.entity.Task;
import com.keshe3.keshe3server.resp.TzResp;
import com.keshe3.keshe3server.service.IMediaService;
import com.keshe3.keshe3server.service.ITaskService;
import com.keshe3.keshe3server.utils.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 媒体表 前端控制器
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-09-06
 */
@RestController
@RequestMapping("media")
public class MediaController {

    @Autowired
    private IMediaService mediaService;

    @Autowired
    private ITaskService taskService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Autowired
    private IdUtils idUtils;

    /**
     * 文件上传
     */
    @PostMapping("upload")
    public TzResp<?> uploadFile(@RequestParam("file") MultipartFile file,
                                @RequestParam("userId") String userId) {
        try {
            // 创建上传目录
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = StringUtils.getFilenameExtension(originalFilename);
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // 保存文件
            Path filePath = Paths.get(uploadDir, uniqueFileName);
            Files.copy(file.getInputStream(), filePath);

            // 判断文件类型（视频为1，图片为0）
            String contentType = file.getContentType();
            String mediaType = "0"; // 默认为图片
            if (contentType != null && contentType.startsWith("video/")) {
                mediaType = "1"; // 视频
            }

            // 创建媒体记录
            Media media = new Media();
            media.setId("me"+idUtils.generateId());
            media.setUserId(userId);
            media.setMediaName(originalFilename);
            media.setMediaType(mediaType); // 修改为数字表示
            media.setMediaSize(file.getSize());
            media.setMediaPath("/" + uploadDir + "/" + uniqueFileName);
            media.setUploadTime(LocalDateTime.now());

            boolean mediaSaved = mediaService.save(media);

            if (mediaSaved) {
                // 创建任务记录
                Task task = new Task();
                task.setId("tk"+idUtils.generateId());
                task.setUserId(userId);
                task.setMediaId(media.getId());
                task.setStartTime(LocalDateTime.now());
                task.setEndTime(LocalDateTime.now());
                task.setTaskStatus("completed");

                taskService.save(task);

                return TzResp.success(media);
            } else {
                // 删除已上传的文件
                Files.deleteIfExists(filePath);
                return TzResp.fail(500, "文件信息保存失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return TzResp.fail(500, "文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return TzResp.fail(500, "系统错误: " + e.getMessage());
        }
    }

    /**
     * 获取用户文件列表
     */
    @PostMapping("list")
    public TzResp<?> getUserFiles(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            if (userId == null || userId.isEmpty()) {
                return TzResp.fail(400, "用户ID不能为空");
            }

            List<Media> mediaList = mediaService.getMediaByUserId(userId);
            return TzResp.success(mediaList);
        } catch (Exception e) {
            e.printStackTrace();
            return TzResp.fail(500, "获取文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    @PostMapping("delete")
    public TzResp<?> deleteFile(@RequestParam("mediaId") String mediaId,
                                @RequestParam("userId") String userId) {
        try {
            Media media = mediaService.getById(mediaId);

            if (media == null) {
                return TzResp.fail(404, "文件不存在");
            }

            if (!media.getUserId().equals(userId)) {
                return TzResp.fail(403, "无权删除此文件");
            }

            // 删除物理文件
            Path filePath = Paths.get(media.getMediaPath().substring(1)); // 移除开头的斜杠
            Files.deleteIfExists(filePath);

            // 删除数据库记录
            boolean deleted = mediaService.removeById(mediaId);

            if (deleted) {
                // 删除相关任务
                taskService.deleteTaskByMediaId(mediaId);
                return TzResp.success(true);
            } else {
                return TzResp.fail(500, "文件删除失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return TzResp.fail(500, "文件删除失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return TzResp.fail(500, "系统错误: " + e.getMessage());
        }
    }
}