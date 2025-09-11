package com.keshe3.keshe3server.service;

import com.keshe3.keshe3server.entity.Task;
import com.baomidou.mybatisplus.extension.service.IService;
import com.keshe3.keshe3server.enums.ETaskStatus;

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

    /**
     * 构建任务信息的方法
     * @param userId 用户ID，用于标识用户身份
     * @param mediaId 媒体ID，用于标识特定的媒体资源
     * @return 返回一个TaskId
     */
    String buildTaskInfo(String userId, String mediaId);

    /**
     * 更新任务状态
     * @param taskId
     * @param status
     * @return
     */
    ETaskStatus updateTaskStatus(String taskId, ETaskStatus status);

    /**
     * 更新任务结束时间
     * @param taskId
     * @return
     */
    boolean updateEndTime(String taskId);
}
