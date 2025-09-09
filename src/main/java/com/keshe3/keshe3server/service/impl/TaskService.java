package com.keshe3.keshe3server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.keshe3.keshe3server.entity.Task;
import com.keshe3.keshe3server.mapper.TaskMapper;
import com.keshe3.keshe3server.service.ITaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 任务表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-09-06
 */
@Service
public class TaskService extends ServiceImpl<TaskMapper, Task> implements ITaskService {
    /**
     * 根据媒体ID删除任务
     * @param mediaId 媒体ID，用于定位要删除的任务
     */
    @Override
    public void deleteTaskByMediaId(String mediaId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getMediaId, mediaId);

        this.remove(wrapper);
    }
}
