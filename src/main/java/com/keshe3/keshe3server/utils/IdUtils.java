package com.keshe3.keshe3server.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdUtils {

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String generateId() {

        // 1. 随机序列号
        String seq = getUUID().substring(0, 4);

        // 2. 生成格式化字符串
        String datePart = DateUtils.dateTimeNow();

        return datePart + seq;
    }
}
