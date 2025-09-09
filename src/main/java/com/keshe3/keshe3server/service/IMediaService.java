package com.keshe3.keshe3server.service;

import com.keshe3.keshe3server.entity.Media;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
