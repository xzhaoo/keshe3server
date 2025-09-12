package com.keshe3.keshe3server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keshe3.keshe3server.entity.ActivityLog;
import com.keshe3.keshe3server.mapper.ActivityLogMapper;
import com.keshe3.keshe3server.service.IActivityLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 活动日志记录表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-09-12
 */
@Service
public class ActivityLogService extends ServiceImpl<ActivityLogMapper, ActivityLog> implements IActivityLogService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 记录用户活动日志并实时推送给管理员
     * @param userId 用户ID
     * @param action 用户操作行为
     * @param details 操作详情
     */
    @Override
    public void logActivity(String userId, String action, String details) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setDetails(details);
        log.setTime(LocalDateTime.now());

        // 保存到数据库
        this.save(log);

        // 实时推送到所有订阅的管理员客户端
        messagingTemplate.convertAndSend("/topic/activities", log);
    }

    @Override
    public List<ActivityLog> getRecentActivities(int number) {

        QueryWrapper<ActivityLog> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("time");
        wrapper.last("LIMIT " + number);

        return list(wrapper);
    }
}
