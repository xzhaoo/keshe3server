package com.keshe3.keshe3server.resp;

import lombok.Data;

@Data
public class UserInfoResp {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户权限
     */
    private String userPermission;

    /**
     * token
     */
    private String token;
}
