package com.zhang.huinongplatform.service;

public interface MessageService {
    
    /**
     * 发送回收进度更新消息
     * @param userId 用户ID
     * @param progress 进度信息
     */
    void sendRecoveryProgressUpdate(Long userId, String progress);
    
    /**
     * 发送回收进度更新消息（带回收记录ID）
     * @param userId 用户ID
     * @param recoveryId 回收记录ID
     * @param progress 进度信息
     */
    void sendRecoveryProgressUpdate(Long userId, Long recoveryId, String progress);
    
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
    
    /**
     * 发送短信通知
     * @param phone 手机号
     * @param templateCode 模板代码
     * @param templateParam 模板参数
     */
    void sendSmsNotification(String phone, String templateCode, String templateParam);
    
    /**
     * 发送验证码短信通知
     * @param phone 手机号
     * @param code 验证码
     */
    void sendVerificationSmsNotification(String phone, String code);
    
    /**
     * 发送业务通知短信
     * @param phone 手机号
     * @param templateParam 模板参数
     */
    void sendBusinessSmsNotification(String phone, String templateParam);
} 