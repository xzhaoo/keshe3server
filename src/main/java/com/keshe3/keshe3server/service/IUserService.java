package com.keshe3.keshe3server.service;

import com.keshe3.keshe3server.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.keshe3.keshe3server.req.UserLoginReq;
import com.keshe3.keshe3server.req.UserSearchReq;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-09-05
 */
public interface IUserService extends IService<User> {

    List<User> search(UserSearchReq req);

    boolean delete(UserSearchReq req);

    boolean changePassword(UserLoginReq req);

    boolean addUser(UserLoginReq req);
}
