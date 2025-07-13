package com.zhang.assistant.common;

import java.util.HashMap;
import java.util.Map;

public class Result<T> {
    private int code;
    private String message;
    private T data;
    private boolean success;
    private long timestamp;

    // 构造方法
    public Result(int code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
        this.timestamp = System.currentTimeMillis();
    }

    // 快捷方法：成功返回
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, true);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, true);
    }

    // 快捷方法：失败返回
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null, false);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null, false);
    }

    // 转换为 Map（可用于日志记录或 JSON 序列化）
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", message);
        map.put("data", data);
        map.put("success", success);
        map.put("timestamp", timestamp);
        return map;
    }

    // Getter and Setter
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", success=" + success +
                ", timestamp=" + timestamp +
                '}';
    }
}