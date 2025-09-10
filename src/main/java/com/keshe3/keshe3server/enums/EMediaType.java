package com.keshe3.keshe3server.enums;

public enum EMediaType {
    PICTURE("01", "picture"),
    VIDEO("02", "video");

    private final String code;

    private final String desc;

    EMediaType(String code, String desc) {
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
