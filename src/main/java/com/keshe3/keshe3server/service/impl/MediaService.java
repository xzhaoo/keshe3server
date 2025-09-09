package com.keshe3.keshe3server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.keshe3.keshe3server.entity.Media;
import com.keshe3.keshe3server.mapper.MediaMapper;
import com.keshe3.keshe3server.service.IMediaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Override
    public List<Media> getMediaByUserId(String userId) {
        LambdaQueryWrapper<Media> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Media::getUserId, userId);
        wrapper.orderByDesc(Media::getUploadTime);
        return this.list(wrapper);
    }
}
