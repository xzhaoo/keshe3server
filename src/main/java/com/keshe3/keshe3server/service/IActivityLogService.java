package com.keshe3.keshe3server.service;

import com.keshe3.keshe3server.entity.ActivityLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 活动日志记录表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-09-12
 */
public interface IActivityLogService extends IService<ActivityLog> {

    /**
     * 记录用户活动的方法
     * @param userId 用户唯一标识ID
     * @param action 用户执行的操作类型
     * @param details 操作的详细信息描述
     */
    public void logActivity(String userId, String action, String details);

    /**
     * 获取最近的活动日志列表
     * 该方法用于查询并返回最近的活动记录
     *
     * @return 返回一个ActivityLog对象列表，包含最近的活动日志信息
     */
    List<ActivityLog> getRecentActivities(int number);
}
