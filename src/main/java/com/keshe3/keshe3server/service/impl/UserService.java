package com.keshe3.keshe3server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.keshe3.keshe3server.entity.User;
import com.keshe3.keshe3server.mapper.UserMapper;
import com.keshe3.keshe3server.req.UserLoginReq;
import com.keshe3.keshe3server.req.UserSearchReq;
import com.keshe3.keshe3server.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keshe3.keshe3server.utils.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-09-05
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private IdUtils idUtils;
    /**
     * 用户查询
     * @param req
     * @return List<User>
     */
    @Override
    public List<User> search(UserSearchReq req) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 查询：用户编号
        String userId = req.getUserId();
        if (userId != null && !userId.isEmpty()) {
            wrapper.eq(User::getId, userId);
        }

        // 用户名模糊查询
        String userName = req.getUserName();
        if (userName != null && !userName.isEmpty()) {
            wrapper.like(User::getUserName, userName);
        }

        // 查询：用户邮箱
        String userEmail = req.getUserEmail();
        if (userEmail != null && !userEmail.isEmpty()) {
            wrapper.like(User::getUserEmail, userEmail);
        }

        Integer index = req.getPageIndex();
        Integer size = req.getPageSize();
        if(index != null && size != null) {
            IPage page = new Page(index, size);
            return list(page, wrapper);
        }
        return list(wrapper);
    }

    /**
     * 用户删除
     * @param req
     * @return boolean
     */
    @Override
    public boolean delete(UserSearchReq req) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 查询：用户编号
        String userId = req.getUserId();
        if (userId != null && !userId.isEmpty()) {
            wrapper.eq(User::getId, userId);
        } else {
            return false;
        }

        // 查询用户
        User user = getOne(wrapper);
        if (user == null) {
            // 用户不存在
            return false;
        }
        return remove(wrapper);
    }

    /**
     * 用户密码修改
     * @param req
     * @return boolean
     */
    @Override
    public boolean changePassword(UserLoginReq req) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 查询：用户名称
        String userName = req.getUserName();
        if (userName != null && !userName.isEmpty()) {
            wrapper.eq(User::getUserName, userName);
        } else {
            return false;
        }

        // 查询用户
        User user = getOne(wrapper);
        if (user == null) {
            // 用户不存在
            return false;
        }

        // 获取新密码
        String newPassword = req.getUserPassword();

        // 更新密码
        user.setUserPassword(newPassword);

        // 保存更新
        return updateById(user);
    }

    /**
     * 增加用户
     * @param req
     * @return boolean
     */
    @Override
    public boolean addUser(UserLoginReq req) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 查询：用户名称
        String userName = req.getUserName();
        wrapper.eq(User::getUserName, userName);

        long count = count(wrapper);
        if (count > 0) {
            // 用户名已存在
            return false;
        }

        // 最大重试次数
        int maxAttempts = 5;
        int attempts = 0;

        while (attempts < maxAttempts) {
            try {
                // 创建新用户对象
                User user = new User();

                // 生成ID
                user.setId("u1"+idUtils.generateId());

                // 设置用户名
                user.setUserName(userName);

                // 设置密码
                user.setUserPassword(req.getUserPassword());

                // 设置邮箱
                user.setUserEmail(req.getUserEmail());

                // 设置用户权限
                user.setUserPermission("0");

                // 保存用户（这里会触发数据库的唯一约束检查）
                boolean result = save(user);

                if (result) {
                    // 保存成功
                    return true;
                }
            } catch (DuplicateKeyException e) {
                // 明确捕获唯一键冲突异常
                log.warn("ID冲突，尝试重新生成ID (尝试次数: " + (attempts + 1) + ")");
            } catch (Exception e) {
                log.error("添加用户时发生未知错误", e);
                // 其他异常，返回失败
                return false;
            }

            attempts++;

            // 短暂等待后重试
            try {
                Thread.sleep(100); // 100毫秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // 所有尝试都失败
        log.error("添加用户失败，已达到最大重试次数: " + maxAttempts);
        return false;
    }
}
