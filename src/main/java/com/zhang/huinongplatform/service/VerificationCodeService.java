package com.zhang.huinongplatform.service;

public interface VerificationCodeService {
    
    /**
     * 发送登录验证码
     * @param phone 手机号
     * @return 是否发送成功
     */
    boolean sendLoginCode(String phone);
    
    /**
     * 验证登录验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 是否验证成功
     */
    boolean verifyLoginCode(String phone, String code);
    
    /**
     * 生成验证码
     * @return 6位数字验证码
     */
    String generateCode();
    
    /**
     * 获取Redis中存储的验证码
     * @param phone 手机号
     * @return 验证码，如果不存在返回null
     */
    String getStoredCode(String phone);
} 