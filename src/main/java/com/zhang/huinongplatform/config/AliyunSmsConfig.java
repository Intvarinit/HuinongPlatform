package com.zhang.huinongplatform.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.sms")
public class AliyunSmsConfig {
    
    private String accessKeyId;
    private String accessKeySecret;
    private String regionId = "cn-hangzhou";
    private String signName;
    private String templateCode;
    private String verificationTemplateCode;
    private String notificationTemplateCode;
    private boolean mockMode = true; // 默认开启模拟模式
    
    @Bean
    public IAcsClient acsClient() {
        // 模拟模式下创建模拟客户端
        if (mockMode) {
            // 返回一个模拟的客户端，实际不会使用
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "mock", "mock");
            return new DefaultAcsClient(profile);
        }
        // 真实模式下创建真实客户端
        DefaultProfile profile = DefaultProfile.getProfile(
            regionId, 
            accessKeyId, 
            accessKeySecret
        );
        return new DefaultAcsClient(profile);
    }
} 