package com.zhang.huinongplatform.service;

public interface SmsService {
    
    /**
     * 发送验证码短信
     * @param phone 手机号
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String phone, String code);
    
    /**
     * 发送通知短信
     * @param phone 手机号
     * @param templateParam 模板参数
     * @return 是否发送成功
     */
    boolean sendNotification(String phone, String templateParam);
    
    /**
     * 发送自定义短信
     * @param phone 手机号
     * @param templateCode 模板代码
     * @param templateParam 模板参数
     * @return 是否发送成功
     */
    boolean sendSms(String phone, String templateCode, String templateParam);
    
    /**
     * 检查是否为模拟模式
     * @return true-模拟模式，false-真实发送模式
     */
    boolean isMockMode();
} 