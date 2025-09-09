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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
                                HttpServletRequest request) {
        Path filePath = null;
        try (InputStream inputStream = file.getInputStream()) {
            // 从请求属性中获取用户ID（由JWT拦截器设置）
            String userId = (String) request.getAttribute("userId");
            if (userId == null || userId.isEmpty()) {
                return TzResp.fail(401, "用户未认证");
            }

            // 创建上传目录
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    return TzResp.fail(500, "创建上传目录失败");
                }
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = StringUtils.getFilenameExtension(originalFilename);
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;

            // 保存文件 - 使用 try-with-resources 确保流关闭
            filePath = Paths.get(uploadDir, uniqueFileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            // 判断文件类型（视频为1，图片为0）
            String contentType = file.getContentType();
            String mediaType = "0"; // 默认为图片
            if (contentType != null && contentType.startsWith("video/")) {
                mediaType = "1"; // 视频
            }

            // 创建媒体记录
            Media media = new Media();
            media.setId("me" + idUtils.generateId());
            media.setUserId(userId);
            media.setMediaName(originalFilename);
            media.setMediaType(mediaType);
            media.setMediaSize(file.getSize());
            media.setMediaPath("/" + uploadDir + "/" + uniqueFileName);
            media.setUploadTime(LocalDateTime.now());

            boolean mediaSaved = mediaService.save(media);

            if (mediaSaved) {
                // 创建任务记录
                Task task = new Task();
                task.setId("tk" + idUtils.generateId());
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
     * 获取用户文件列表 - 修改为从token获取用户ID
     */
    @PostMapping("list")
    public TzResp<?> getUserFiles(HttpServletRequest request) {
        try {
            // 从请求属性中获取用户ID（由JWT拦截器设置）
            String userId = (String) request.getAttribute("userId");
            if (userId == null || userId.isEmpty()) {
                return TzResp.fail(401, "用户未认证");
            }

            List<Media> mediaList = mediaService.getMediaByUserId(userId);
            return TzResp.success(mediaList);
        } catch (Exception e) {
            e.printStackTrace();
            return TzResp.fail(500, "获取文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 文件预览
     */
    @GetMapping("preview/{mediaId}")
    public void previewFile(@PathVariable String mediaId, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 从请求属性中获取用户ID（由JWT拦截器设置）
            String userId = (String) request.getAttribute("userId");
            if (userId == null || userId.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Media media = mediaService.getById(mediaId);
            if (media == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 检查用户是否有权限访问此文件
            if (!media.getUserId().equals(userId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // 获取文件路径
            String filePath = media.getMediaPath().substring(1); // 移除开头的斜杠
            Path path = Paths.get(filePath);

            if (!Files.exists(path)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 设置响应头
            String contentType = Files.probeContentType(path);
            System.out.println(contentType);
            if (contentType == null) {
                // 根据文件扩展名设置明确的MIME类型
                String fileName = media.getMediaName().toLowerCase();
                System.out.println(fileName);
                if (fileName.endsWith(".png")) {
                    contentType = "image/png";
                } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (fileName.endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (fileName.endsWith(".bmp")) {
                    contentType = "image/bmp";
                } else if (fileName.endsWith(".mp4")) {
                    contentType = "video/mp4";
                } else if (fileName.endsWith(".avi")) {
                    contentType = "video/avi";
                } else if (fileName.endsWith(".mov")) {
                    contentType = "video/quicktime";
                } else {
                    contentType = "application/octet-stream";
                }
            }

            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline; filename=\"" + media.getMediaName() + "\"");
            response.setContentLengthLong(Files.size(path));

            // 将文件内容写入响应
            Files.copy(path, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 文件下载
     */
    @GetMapping("download/{mediaId}")
    public void downloadFile(@PathVariable String mediaId, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 从请求属性中获取用户ID（由JWT拦截器设置）
            String userId = (String) request.getAttribute("userId");
            if (userId == null || userId.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Media media = mediaService.getById(mediaId);
            if (media == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 检查用户是否有权限访问此文件
            if (!media.getUserId().equals(userId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // 获取文件路径
            String filePath = media.getMediaPath().substring(1); // 移除开头的斜杠
            Path path = Paths.get(filePath);

            if (!Files.exists(path)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + media.getMediaName() + "\"");
            response.setContentLengthLong(Files.size(path));

            // 将文件内容写入响应
            Files.copy(path, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除文件 - 修改为从token获取用户ID
     */
    @PostMapping("delete")
    public TzResp<?> deleteFile(@RequestParam("mediaId") String mediaId,
                                HttpServletRequest request) { // 移除userId参数，改为从request获取
        try {
            // 从请求属性中获取用户ID（由JWT拦截器设置）
            String userId = (String) request.getAttribute("userId");
            if (userId == null || userId.isEmpty()) {
                return TzResp.fail(401, "用户未认证");
            }

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