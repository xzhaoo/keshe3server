package com.keshe3.keshe3server.enums;

public enum EUserPermission {
    ADMIN("01", "管理员"),
    USER("02", "普通用户");

    private final String code;

    private final String desc;

    EUserPermission(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
