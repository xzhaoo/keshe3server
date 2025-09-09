package com.keshe3.keshe3server.service;

import com.keshe3.keshe3server.entity.Task;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 任务表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-09-06
 */
public interface ITaskService extends IService<Task> {
    /**
     * 根据媒体ID删除任务
     * 该方法用于删除与指定媒体ID相关联的任务
     *
     * @param mediaId 媒体ID，用于标识需要删除的任务
     */
    void deleteTaskByMediaId(String mediaId);
}
