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

    /**
     * 根据用户搜索请求条件查询用户列表
     *
     * @param req 用户搜索请求参数，包含查询条件
     * @return 返回符合查询条件的用户列表
     */
    List<User> search(UserSearchReq req);

    /**
     * 删除用户的方法
     * 根据用户搜索请求条件删除匹配的用户信息
     *
     * @param req 用户搜索请求对象，包含删除所需的条件信息
     * @return 删除操作是否成功，true表示成功，false表示失败
     */
    boolean delete(UserSearchReq req);

    /**
     * 修改用户密码的方法
     * @param req 包含用户登录信息的请求对象，用于验证用户身份并设置新密码
     * @return 修改密码操作是否成功，成功返回true，失败返回false
     */
    boolean changePassword(UserLoginReq req);

    /**
     * 添加用户的方法
     * @param req 用户登录请求对象，包含用户相关信息
     * @return 添加成功返回true，失败返回false
     */
    boolean addUser(UserLoginReq req);

    /**
     * 获取用户总数的方法
     *
     * @return 返回系统中用户的总数量，类型为long
     */
    long getTotalUserCount();
}
