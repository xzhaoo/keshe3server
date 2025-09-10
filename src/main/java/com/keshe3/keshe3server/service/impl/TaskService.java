package com.keshe3.keshe3server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.keshe3.keshe3server.entity.Task;
import com.keshe3.keshe3server.enums.ETaskStatus;
import com.keshe3.keshe3server.mapper.TaskMapper;
import com.keshe3.keshe3server.service.ITaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keshe3.keshe3server.utils.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    @Autowired
    private IdUtils idUtils;

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

    /**
     * 构建任务信息并保存
     * @param userId 用户ID
     * @param mediaId 媒体ID
     * @return 构建并保存成功的任务对象，如果保存失败则返回null
     */
    @Override
    public Task buildTaskInfo(String userId, String mediaId) {
        Task task = new Task();
        task.setId("tk" + idUtils.generateId());
        task.setUserId(userId);
        task.setMediaId(mediaId);
        task.setStartTime(LocalDateTime.now());
        task.setTaskStatus("01");

        if(save(task)) {
            return task;
        }
        return null;
    }

    /**
     * 更新任务状态的方法
     * @param taskId 任务ID
     * @param status 任务状态枚举
     * @return 更新成功返回新的任务状态，失败返回null
     */
    @Override
    public ETaskStatus updateTaskStatus(String taskId, ETaskStatus status) {
        Task task = getById(taskId);
        if(task == null) {
            return null;
        }

        task.setTaskStatus(status.getCode());
        if(updateById(task)) {
            return status;
        }
        return null;
    }


    /**
     * 更新指定任务的结束时间
     * @param taskId 要更新结束时间的任务ID
     * @return 更新成功返回true，如果任务不存在则返回false
     */
    @Override
    public boolean updateEndTime(String taskId) {
        Task task = getById(taskId);
        if(task == null) {
            return false;
        }

        task.setEndTime(LocalDateTime.now());
        return updateById(task);
    }
}
