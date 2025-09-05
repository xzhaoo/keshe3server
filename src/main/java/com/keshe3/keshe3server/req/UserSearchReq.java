package com.keshe3.keshe3server.req;

import lombok.Data;

@Data
public class UserSearchReq {

    /**
     * 用户编号
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
     * 分页下标
     */
    private Integer pageIndex;

    /**
     * 分页大小
     */
    private Integer pageSize;
}
