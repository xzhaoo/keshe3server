package com.keshe3.keshe3server.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdUtils {

    /**
     * 生成一个没有横线的UUID字符串
     * @return 返回一个32位的UUID字符串，不包含横线
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成唯一ID的方法
     * 该方法结合当前时间和随机序列号生成一个唯一的标识符
     *
     * @return 返回一个由日期时间和随机序列号组成的字符串
     */
    public String generateId() {

        // 1. 随机序列号
        String seq = getUUID().substring(0, 4);

        // 2. 生成格式化字符串
        String datePart = DateUtils.dateTimeNow();

        // 将日期部分和随机序列号拼接并返回
        return datePart + seq;
    }
}
