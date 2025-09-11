package com.keshe3.keshe3server.service;

import com.keshe3.keshe3server.entity.Media;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 媒体表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-09-06
 */
public interface IMediaService extends IService<Media> {
    List<Media> getMediaByUserId(String userId);

    /**
     * 根据搜索请求查询媒体资源
     *
     * @param mediaSearchReq 媒体搜索请求参数，包含搜索条件
     * @return 返回匹配条件的媒体资源列表
     */
    List<Media> searchMedia(String mediaSearchReq);

    /**
     * 保存临时文件的方法
     * @param file 需要保存的临时文件对象，通过MultipartFile接口接收
     * @return 保存成功返回true，保存失败返回false
     */
    boolean saveTempFile(MultipartFile file);

    /**
     * 构建媒体信息的方法
     * @param userId 用户ID，用于标识创建媒体信息的用户
     * @param mediaType 媒体类型，如视频、音频、图片等
     * @param mediaName 媒体名称，媒体资源的标识名称
     * @param mediaSize 媒体大小，以字节为单位表示的媒体文件大小
     * @return 返回布尔值，表示媒体信息是否构建成功
     */
    String buildMediaInfo(String userId, String mediaType, String mediaName, long mediaSize);

    /**
     * 设置媒体文件路径的方法
     * @param mediaId 媒体文件的唯一标识符
     * @param mediaPath 媒体文件的存储路径
     * @return 设置成功返回true，设置失败返回false
     */
    boolean setMediaPath(String mediaId, String mediaPath);
}
