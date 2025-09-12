package com.keshe3.keshe3server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.keshe3.keshe3server.entity.Media;
import com.keshe3.keshe3server.mapper.MediaMapper;
import com.keshe3.keshe3server.service.IMediaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keshe3.keshe3server.utils.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 媒体表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-09-06
 */
@Service
public class MediaService extends ServiceImpl<MediaMapper, Media> implements IMediaService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Autowired
    private IdUtils idUtils;

    @Override
    /**
     * 根据用户ID获取该用户的所有媒体文件列表
     * @param userId 用户ID，用于筛选特定用户的媒体文件
     * @return 返回按上传时间降序排列的媒体文件列表
     */
    public List<Media> getMediaByUserId(String userId) {
        LambdaQueryWrapper<Media> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Media::getUserId, userId);
        wrapper.orderByDesc(Media::getUploadTime);

        return this.list(wrapper);
    }

    /**
     * 搜索媒体
     * @param mediaSearchReq
     * @return
     */
    @Override
    public List<Media> searchMedia(String mediaSearchReq) {
        LambdaQueryWrapper<Media> wrapper = new LambdaQueryWrapper<>();
        return List.of();
    }

    /**
     * 保存临时文件
     * @param file
     * @return
     */
    @Override
    public boolean saveTempFile(MultipartFile file) {
        Path filePath = null;

        try (InputStream inputStream = file.getInputStream()) {

            // 创建上传目录
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    return false;
                }
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = StringUtils.getFilenameExtension(originalFilename);
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;

            // 保存文件 - 使用 try-with-resources 确保流关闭
            filePath = Paths.get(uploadDir, uniqueFileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 构建媒体信息
     * @param userId
     * @param mediaType
     * @param mediaName
     * @param mediaSize
     * @return
     */
    @Override
    public String buildMediaInfo(String userId, String mediaType, String mediaName, long mediaSize) {
        Media media = new Media();
        media.setId("me" + idUtils.generateId());
        media.setUserId(userId);
        media.setMediaName(mediaName);
        media.setMediaType(mediaType);
        media.setMediaSize(mediaSize);
        media.setUploadTime(LocalDateTime.now());
        if(save(media)) {
            return media.getId();
        }
        return null;
    }

    /**
     * 设置媒体资源的路径
     * @param mediaId 媒体资源的唯一标识ID
     * @param mediaPath 媒体资源的存储路径
     * @return 更新成功返回true，失败返回false
     */
    @Override
    public boolean setMediaPath(String mediaId, String mediaPath) {
        Media media = getById(mediaId);
        if(media != null) {
            media.setMediaPath(mediaPath);
            return updateById(media);
        }

        return false;
    }

    /**
     * 获取媒体总数的方法
     *
     * @return long 返回媒体的总数量
     */
    @Override
    public long getTotalMediaCount() {
        return count();
    }

    /**
     * 获取总存储大小的方法
     *
     * @return 返回总存储大小的长整型数值
     */
    @Override
    public long getTotalStorageSize() {
        return getBaseMapper().selectTotalStorageSize();
    }
}
