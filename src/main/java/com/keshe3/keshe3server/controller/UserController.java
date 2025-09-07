package com.keshe3.keshe3server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.keshe3.keshe3server.entity.User;
import com.keshe3.keshe3server.req.UserLoginReq;
import com.keshe3.keshe3server.req.UserSearchReq;
import com.keshe3.keshe3server.resp.TzResp;
import com.keshe3.keshe3server.resp.UserInfoResp;
import com.keshe3.keshe3server.service.IUserService;
import com.keshe3.keshe3server.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 权限检查
     */
    private boolean checkAdminPermission(HttpServletRequest request) {
        String userPermission = (String) request.getAttribute("userPermission");
        return "1".equals(userPermission);
    }

    /**
     * 登录
     */
    @PostMapping("login")
    public TzResp<UserInfoResp> login(UserLoginReq req) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName, req.getUserName());
        wrapper.eq(User::getUserPassword, req.getUserPassword());

        User user = userService.getOne(wrapper);

        if (user != null) {
            // 生成JWT Token
            String token = jwtUtils.generateToken(user.getId(), user.getUserName(), user.getUserPermission());

            // 创建并填充 UserInfoResp
            UserInfoResp userInfoResp = new UserInfoResp();
            userInfoResp.setUserId(user.getId());
            userInfoResp.setUserName(user.getUserName());
            userInfoResp.setUserEmail(user.getUserEmail());
            userInfoResp.setUserPermission(user.getUserPermission());
            userInfoResp.setToken(token); // 添加token字段

            return TzResp.success(userInfoResp);
        }

        // 登录失败，返回空对象或null
        return TzResp.fail(201);
    }

    /**
     * 注册
     */
    @PostMapping("register")
    public TzResp<Boolean> register(UserLoginReq req) {
        return TzResp.success(userService.addUser(req));
    }

    /**
     * 查询用户信息
     */
    @PostMapping("search")
    public TzResp<List<User>> search(UserSearchReq req, HttpServletRequest request) {
        if (!checkAdminPermission(request)) {
            return TzResp.fail(403); // 无权限
        }
        return TzResp.success(userService.search(req));
    }

    /**
     * 删除用户
     */
    @PostMapping("delete")
    public TzResp<Boolean> delete(UserSearchReq req, HttpServletRequest request) {
        if (!checkAdminPermission(request)) {
            return TzResp.fail(403, false); // 无权限
        }
        return TzResp.success(userService.delete(req));
    }

    /**
     * 修改用户密码
     */
    @PostMapping("changePassword")
    public TzResp<String> changePassword(UserLoginReq req, HttpServletRequest request) {
        if (req.getUserName().equals(request.getAttribute("username"))) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUserName, req.getUserName());
            wrapper.eq(User::getUserPassword, req.getUserEmail());

            User user = userService.getOne(wrapper);

            if (user == null) {
                return TzResp.fail(201, "用户不存在或密码错误");
            }
            System.out.println(req);
            return TzResp.success(String.valueOf(userService.changePassword(req)));
        }

        return TzResp.fail(403, "无操作权限"); // 无权限
    }

    /**
     * 重置用户密码
     */
    @PostMapping("resetPassword")
    public TzResp<Boolean> resetPassword(UserLoginReq req, HttpServletRequest request) {
        if (!checkAdminPermission(request)) {
            return TzResp.fail(403, false); // 无权限
        }
        req.setUserPassword("123456");
        return TzResp.success(userService.changePassword(req));
    }
}
