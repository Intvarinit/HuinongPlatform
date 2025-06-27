package com.zhang.huinongplatform.service;

public interface MessageService {
    
    /**
     * 发送回收进度更新消息
     * @param userId 用户ID
     * @param progress 进度信息
     */
    void sendRecoveryProgressUpdate(Long userId, String progress);
    
    /**
     * 发送用户验证码
     * @param phone 手机号
     * @param code 验证码
     */
    void sendVerificationCode(String phone, String code);
    
    /**
     * 发送数据同步消息
     * @param data 同步数据
     */
    void sendDataSync(Object data);
    
    /**
     * 发送抽检异常通知
     * @param message 消息内容
     */
    void sendInspectionNotification(java.util.Map<String, Object> message);
} 