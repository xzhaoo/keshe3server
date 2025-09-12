package com.keshe3.keshe3server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.keshe3.keshe3server.entity.User;
import com.keshe3.keshe3server.enums.EUserPermission;
import com.keshe3.keshe3server.req.UserLoginReq;
import com.keshe3.keshe3server.req.UserSearchReq;
import com.keshe3.keshe3server.resp.TzResp;
import com.keshe3.keshe3server.resp.UserInfoResp;
import com.keshe3.keshe3server.service.IActivityLogService;
import com.keshe3.keshe3server.service.IMediaService;
import com.keshe3.keshe3server.service.IUserService;
import com.keshe3.keshe3server.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-09-05
 */
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private IMediaService mediaService;

    @Autowired
    private IActivityLogService activityLogService;

    /**
     * 权限检查方法
     * 检查当前用户是否具有管理员权限
     * @param request HttpServletRequest对象，包含当前请求信息
     * @return 如果用户权限为"1"则返回true，表示有管理员权限；否则返回false
     */
    private boolean checkAdminPermission(HttpServletRequest request) {
        // 从请求属性中获取用户权限信息
        String userPermission = (String) request.getAttribute("userPermission");
        // 检查用户权限是否为"01"，如果是则返回true，否则返回false
        return EUserPermission.ADMIN.getCode().equals(userPermission);
    }

    /**
     * 登录接口
    * 处理用户登录请求，验证用户名和密码，验证成功后生成JWT Token并返回用户信息
     */
    @PostMapping("login")
    public TzResp<UserInfoResp> login(UserLoginReq req) {
        // 创建Lambda查询条件构造器，用于查询数据库中的用户信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName, req.getUserName());
        wrapper.eq(User::getUserPassword, req.getUserPassword());

        // 根据查询条件从数据库中获取用户信息
        User user = userService.getOne(wrapper);

        // 如果用户存在（登录成功）
        if (user != null) {
            // 生成JWT Token，包含用户ID、用户名和权限信息
            String token = jwtUtils.generateToken(user.getId(), user.getUserName(), user.getUserPermission());

            // 创建并填充 UserInfoResp
            UserInfoResp userInfoResp = new UserInfoResp();
            userInfoResp.setUserId(user.getId());
            userInfoResp.setUserName(user.getUserName());
            userInfoResp.setUserEmail(user.getUserEmail());
            userInfoResp.setUserPermission(user.getUserPermission());
            userInfoResp.setToken(token);

            activityLogService.logActivity(user.getId(), "登录", "用户 " + user.getUserName() + "登录了");
            return TzResp.success(userInfoResp);
        }

        // 登录失败，返回空对象或null
        return TzResp.fail(201);
    }

    /**
     * 注册接口
     * 该接口用于处理用户注册请求
     * @param req 用户登录请求参数，包含用户注册所需信息
     * @return 返回操作结果，包含是否注册成功的状态信息
     */
    @PostMapping("register")
    public TzResp<Boolean> register(UserLoginReq req) {
        String userId = userService.addUser(req);
        if(userService.addUser(req) != null) {
            activityLogService.logActivity(userId, "注册", "用户 " + req.getUserName() + "注册成功");
            return TzResp.success(true);
        }

        return TzResp.fail(500,false);
    }

    /**
     * 查询用户信息
     * 该接口用于管理员查询用户信息，需要管理员权限验证
     * @param req 用户搜索请求参数对象，包含查询条件
     * @param request HTTP请求对象，用于权限验证
     * @return TzResp<List<User>> 返回用户列表的响应对象
     *         成功时返回状态码200和用户列表数据
     *         无权限时返回状态码403
     */
    @PostMapping("search")
    public TzResp<List<User>> search(UserSearchReq req, HttpServletRequest request) {
        // 检查管理员权限
        if (!checkAdminPermission(request)) {
            return TzResp.fail(403);
        }
        return TzResp.success(userService.search(req));
    }

    /**
     * 删除用户
     * 该接口用于删除用户，需要管理员权限才能执行
     * @PostMapping("delete") 表示这是一个POST请求，映射到"/delete"路径
     *
     * @param req 用户搜索请求对象，包含要删除的用户信息
     * @param request HTTP请求对象，用于获取请求信息和权限验证
     * @return TzResp<Boolean> 返回操作结果，包含状态码和是否删除成功
     *
     * 方法逻辑：
     * 1. 首先检查当前用户是否具有管理员权限
     * 2. 如果没有管理员权限，返回失败状态码403和false
     * 3. 如果有权限，调用userService.delete(req)执行删除操作
     * 4. 返回操作结果，成功则返回true，失败则返回false
     */
    @PostMapping("delete")
    public TzResp<Boolean> delete(UserSearchReq req, HttpServletRequest request) {
        if (!checkAdminPermission(request)) {
            return TzResp.fail(403, false); // 无权限
        }

        activityLogService.logActivity(req.getUserId(), "删除", "管理员删除了用户 " + req.getUserName());
        return TzResp.success(userService.delete(req));
    }

    /**
     * 修改用户密码
     * 这是一个处理HTTP POST请求的方法，用于修改用户密码
     * @param req 包含用户名和密码等信息的请求对象
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 返回一个TzResp对象，包含操作结果信息
     */
    @PostMapping("changePassword")
    public TzResp<String> changePassword(UserLoginReq req, HttpServletRequest request) {
        // 验证当前用户是否有权限修改自己的密码
        if (req.getUserName().equals(request.getAttribute("username"))) {
            // 创建查询条件，根据用户名和原始密码查询用户
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUserName, req.getUserName());
            wrapper.eq(User::getUserPassword, req.getUserEmail());

            // 执行查询，获取用户信息
            User user = userService.getOne(wrapper);

            // 如果用户不存在或密码错误，返回错误信息
            if (user == null) {
                return TzResp.fail(201, "用户不存在或密码错误");
            }

            String userId = userService.changePassword(req);
            if (userId != null) {
                activityLogService.logActivity(userId, "修改密码", "用户 " + req.getUserName() + "修改了密码");
                return TzResp.success("true");
            }

            return TzResp.fail(500, "修改密码失败");
        }

        return TzResp.fail(403, "无操作权限"); // 无权限
    }

    /**
     * 重置用户密码
     * 管理员专用接口，用于重置指定用户的密码
     *
     * @param req 包含用户登录信息的请求对象，用于标识需要重置密码的用户
     * @param request HTTP请求对象，用于获取当前登录用户信息和权限验证
     * @return 返回操作结果，TzResp类型封装了操作状态和数据
     *         成功时返回true，失败时返回false并附带错误码
     */
    @PostMapping("resetPassword")  // 标识为POST请求，映射到/resetPassword路径
    public TzResp<Boolean> resetPassword(UserLoginReq req, HttpServletRequest request) {
        if (!checkAdminPermission(request)) {
            return TzResp.fail(403, false);
        }

        req.setUserPassword("123456");
        String userId = userService.changePassword(req);
        if (userId != null) {
            activityLogService.logActivity(userId, "重置密码", "管理员重置了用户 " + req.getUserName() + "的密码");
            return TzResp.success(true);
        }
        return TzResp.success();
    }

    /**
     * 获取仪表盘统计数据接口
     * @param request HTTP请求对象，用于权限验证
     * @return 返回包含用户总数、媒体总数和存储使用情况的统计信息
     */
    @PostMapping("dashboardStats")
    public TzResp<?> getDashboardStats(HttpServletRequest request) {
        if (!checkAdminPermission(request)) {
            return TzResp.fail(403, null);
        }

        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", userService.getTotalUserCount());
            stats.put("totalMedia", mediaService.getTotalMediaCount());

            // 将字节转换为GB
            long totalStorageBytes = mediaService.getTotalStorageSize();
            double totalStorageGB = totalStorageBytes / (1024.0 * 1024.0 * 1024.0);
            stats.put("storageUsed", String.format("%.2f", totalStorageGB));

            stats.put("recentActivities", activityLogService.getRecentActivities(10));

            return TzResp.success(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return TzResp.fail(500, "获取统计信息失败: " + e.getMessage());
        }
    }
}
