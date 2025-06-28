package com.zhang.huinongplatform.service.impl;

import com.zhang.huinongplatform.service.MessageService;
import com.zhang.huinongplatform.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final MessageService messageService;
    private final StringRedisTemplate redisTemplate;
    
    // Redis key前缀
    private static final String LOGIN_CODE_PREFIX = "login:code:";
    // 验证码有效期（分钟）
    private static final long CODE_EXPIRE_MINUTES = 5;
    // 验证码长度
    private static final int CODE_LENGTH = 6;

    @Override
    public boolean sendLoginCode(String phone) {
        try {
            // 生成验证码
            String code = generateCode();
            
            // 将验证码存储到Redis，设置过期时间
            String redisKey = LOGIN_CODE_PREFIX + phone;
            redisTemplate.opsForValue().set(redisKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            // 通过消息队列发送验证码短信
            messageService.sendVerificationSmsNotification(phone, code);
            
            log.info("登录验证码发送成功 - 手机号: {}, 验证码: {}, 过期时间: {}分钟", 
                    phone, code, CODE_EXPIRE_MINUTES);
            return true;
            
        } catch (Exception e) {
            log.error("发送登录验证码异常 - 手机号: {}, 错误: {}", phone, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean verifyLoginCode(String phone, String code) {
        try {
            // 从Redis获取存储的验证码
            String storedCode = getStoredCode(phone);
            if (storedCode == null) {
                log.warn("验证码不存在或已过期 - 手机号: {}", phone);
                return false;
            }
            
            // 验证验证码
            boolean isValid = storedCode.equals(code);
            if (isValid) {
                // 验证成功后删除验证码，防止重复使用
                String redisKey = LOGIN_CODE_PREFIX + phone;
                redisTemplate.delete(redisKey);
                log.info("验证码验证成功 - 手机号: {}", phone);
            } else {
                log.warn("验证码验证失败 - 手机号: {}, 输入验证码: {}, 存储验证码: {}", 
                        phone, code, storedCode);
            }
            
            return isValid;
            
        } catch (Exception e) {
            log.error("验证登录验证码异常 - 手机号: {}, 错误: {}", phone, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    @Override
    public String getStoredCode(String phone) {
        String redisKey = LOGIN_CODE_PREFIX + phone;
        return redisTemplate.opsForValue().get(redisKey);
    }
} 