package com.keshe3.keshe3server.enums;

public enum ETaskStatus {
    PENDING("01", "待处理"),
    PROCESSING("02", "处理中"),
    COMPLETED("03", "已完成"),
    FAILED("04", "失败");

    private final String code;

    private final String desc;

    ETaskStatus(String code, String desc) {
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