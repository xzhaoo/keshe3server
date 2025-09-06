package com.keshe3.keshe3server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.keshe3.keshe3server.entity.User;
import com.keshe3.keshe3server.req.UserLoginReq;
import com.keshe3.keshe3server.req.UserSearchReq;
import com.keshe3.keshe3server.resp.TzResp;
import com.keshe3.keshe3server.resp.UserInfoResp;
import com.keshe3.keshe3server.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            System.out.println("登录成功");

            // 创建并填充 UserInfoResp
            UserInfoResp userInfoResp = new UserInfoResp();
            userInfoResp.setUserId(user.getId());
            userInfoResp.setUserName(user.getUserName());
            userInfoResp.setUserEmail(user.getUserEmail());

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
    public TzResp<List<User>> search(UserSearchReq req) {
        return TzResp.success(userService.search(req));
    }

    /**
     * 删除用户
     */
    @PostMapping("delete")
    public TzResp<Boolean> delete(UserSearchReq req) {
        return TzResp.success(userService.delete(req));
    }

    /**
     * 修改用户密码
     */
    @PostMapping("changePassword")
    public TzResp<Boolean> changePassword(UserLoginReq req) {
        return TzResp.success(userService.changePassword(req));
    }

    /**
     * 重置用户密码
     */
    @PostMapping("resetPassword")
    public TzResp<Boolean> resetPassword(UserLoginReq req) {
        req.setUserPassword("123456");
        return TzResp.success(userService.changePassword(req));
    }
}
