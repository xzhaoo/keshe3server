package com.keshe3.keshe3server.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期时间工具类
 */
public class DateUtils {

    private static final String DEFAULT_PATTERN = "yyyyMMddHHmmss";

    /**
     * 获取当前日期时间字符串（默认格式：yyyyMMddHHmmss）
     * @return 格式化后的日期时间字符串
     */
    public static String dateTimeNow() {
        return dateTimeNow(DEFAULT_PATTERN);
    }

    /**
     * 获取当前日期时间字符串（指定格式）
     * @param pattern 日期格式
     * @return 格式化后的日期时间字符串
     */
    public static String dateTimeNow(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    /**
     * 日期格式化
     * @param date 日期对象
     * @param pattern 日期格式
     * @return 格式化后的日期字符串
     */
    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 获取当前日期字符串（yyyyMMdd）
     * @return 格式化后的日期字符串
     */
    public static String dateNow() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    /**
     * 获取当前时间字符串（HHmmss）
     * @return 格式化后的时间字符串
     */
    public static String timeNow() {
        return new SimpleDateFormat("HHmmss").format(new Date());
    }
}