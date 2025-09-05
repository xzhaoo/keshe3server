package com.keshe3.keshe3server.req;

import lombok.Data;

@Data
public class UserLoginReq {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户密码
     */
    private String userPassword;
}
