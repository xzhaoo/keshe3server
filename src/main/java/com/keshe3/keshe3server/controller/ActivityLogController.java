package com.keshe3.keshe3server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keshe3.keshe3server.entity.ActivityLog;
import com.keshe3.keshe3server.enums.EUserPermission;
import com.keshe3.keshe3server.resp.TzResp;
import com.keshe3.keshe3server.service.IActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* <p>
* 活动日志记录表 前端控制器
* </p>
*
* @author CodeGenerator
* @since 2025-09-12
*/
@RestController
@RequestMapping("activity-log")
public class ActivityLogController {

    @Autowired
    private IActivityLogService activityLogService;

    /**
     * 获取最近的活动日志
     */
    @PostMapping("recent")
    public TzResp<?> getRecentActivities(
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            HttpServletRequest request) {

        // 检查管理员权限
        String userPermission = (String) request.getAttribute("userPermission");
        if (!EUserPermission.ADMIN.getCode().equals(userPermission)) {
            return TzResp.fail(403, "无权限访问");
        }

        QueryWrapper<ActivityLog> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("time");
        wrapper.last("LIMIT " + limit);

        List<ActivityLog> activities = activityLogService.list(wrapper);
        return TzResp.success(activities);
    }

    /**
     * 分页查询活动日志
     */
    @PostMapping("page")
    public TzResp<?> getActivityLogPage(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest request) {

        // 检查管理员权限
        String userPermission = (String) request.getAttribute("userPermission");
        if (!EUserPermission.ADMIN.getCode().equals(userPermission)) {
            return TzResp.fail(403, "无权限访问");
        }

        Page<ActivityLog> pageParam = new Page<>(page, size);
        QueryWrapper<ActivityLog> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("time");

        IPage<ActivityLog> result = activityLogService.page(pageParam, wrapper);
        return TzResp.success(result);
    }
}