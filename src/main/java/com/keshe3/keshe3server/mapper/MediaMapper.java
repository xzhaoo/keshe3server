package com.keshe3.keshe3server.mapper;

import com.keshe3.keshe3server.entity.Media;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 媒体表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-09-06
 */
public interface MediaMapper extends BaseMapper<Media> {

    /**
     * 计算所有媒体文件的总大小（字节）
     * 使用聚合查询，性能优化版本
     * @return 总字节数
     */
    @Select("SELECT COALESCE(SUM(media_size), 0) FROM media")
    long selectTotalStorageSize();
}
