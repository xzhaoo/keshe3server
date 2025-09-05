package com.keshe3.keshe3server.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class TzResp<T> implements Serializable {
    private static final long serialVersionUID = 37320250905L;

    /**
     * 状态码
     */
    private int code = 200;

    /**
     * 内容
     */
    private T data;

    public TzResp(int code) {
        this.code = code;
    }

    public TzResp(int code, T data) {
        this.code = code;
        this.data = data;
    }

    /**
     * 判断请求是否成功
     */
    @JsonIgnore
    public boolean isSuccess() {
        return this.code == 200;
    }

    /**
     * 成功响应，无数据
     */
    public static <T> TzResp<T> success() {
        return new TzResp<>(200);
    }

    /**
     * 成功响应，带数据
     */
    public static <T> TzResp<T> success(T data) {
        return new TzResp<>(200, data);
    }

    /**
     * 失败响应
     */
    public static <T> TzResp<T> fail(int code) {
        return new TzResp<>(code);
    }

    /**
     * 失败响应，带数据
     */
    public static <T> TzResp<T> fail(int code, T data) {
        return new TzResp<>(code, data);
    }
}
