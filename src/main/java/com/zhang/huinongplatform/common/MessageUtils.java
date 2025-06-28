package com.zhang.huinongplatform.common;

import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
public class MessageUtils {
    
    /**
     * 安全地从消息中获取Long类型的值
     * @param message 消息Map
     * @param key 键名
     * @return Long值，如果转换失败返回null
     */
    public static Long getLongValue(Map<String, Object> message, String key) {
        Object value = message.get(key);
        if (value == null) {
            return null;
        }
        
        try {
            if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof Integer) {
                return ((Integer) value).longValue();
            } else if (value instanceof String) {
                return Long.valueOf((String) value);
            } else {
                log.warn("无法将值转换为Long类型 - key: {}, value: {}, type: {}", 
                        key, value, value.getClass().getName());
                return null;
            }
        } catch (NumberFormatException e) {
            log.warn("数值格式转换失败 - key: {}, value: {}", key, value);
            return null;
        }
    }
    
    /**
     * 安全地从消息中获取Integer类型的值
     * @param message 消息Map
     * @param key 键名
     * @return Integer值，如果转换失败返回null
     */
    public static Integer getIntegerValue(Map<String, Object> message, String key) {
        Object value = message.get(key);
        if (value == null) {
            return null;
        }
        
        try {
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Long) {
                return ((Long) value).intValue();
            } else if (value instanceof String) {
                return Integer.valueOf((String) value);
            } else {
                log.warn("无法将值转换为Integer类型 - key: {}, value: {}, type: {}", 
                        key, value, value.getClass().getName());
                return null;
            }
        } catch (NumberFormatException e) {
            log.warn("数值格式转换失败 - key: {}, value: {}", key, value);
            return null;
        }
    }
    
    /**
     * 安全地从消息中获取String类型的值
     * @param message 消息Map
     * @param key 键名
     * @return String值，如果转换失败返回null
     */
    public static String getStringValue(Map<String, Object> message, String key) {
        Object value = message.get(key);
        if (value == null) {
            return null;
        }
        
        if (value instanceof String) {
            return (String) value;
        } else {
            return value.toString();
        }
    }
    
    /**
     * 安全地从消息中获取Double类型的值
     * @param message 消息Map
     * @param key 键名
     * @return Double值，如果转换失败返回null
     */
    public static Double getDoubleValue(Map<String, Object> message, String key) {
        Object value = message.get(key);
        if (value == null) {
            return null;
        }
        
        try {
            if (value instanceof Double) {
                return (Double) value;
            } else if (value instanceof Float) {
                return ((Float) value).doubleValue();
            } else if (value instanceof Integer) {
                return ((Integer) value).doubleValue();
            } else if (value instanceof Long) {
                return ((Long) value).doubleValue();
            } else if (value instanceof String) {
                return Double.valueOf((String) value);
            } else {
                log.warn("无法将值转换为Double类型 - key: {}, value: {}, type: {}", 
                        key, value, value.getClass().getName());
                return null;
            }
        } catch (NumberFormatException e) {
            log.warn("数值格式转换失败 - key: {}, value: {}", key, value);
            return null;
        }
    }
} 