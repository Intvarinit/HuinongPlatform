package com.zhang.huinongplatform.service.impl;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhang.huinongplatform.config.AliyunSmsConfig;
import com.zhang.huinongplatform.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    private final AliyunSmsConfig smsConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 使用@Autowired(required = false)允许注入null
    @Autowired(required = false)
    private IAcsClient acsClient;

    public SmsServiceImpl(AliyunSmsConfig smsConfig) {
        this.smsConfig = smsConfig;
    }

    @Override
    public boolean sendVerificationCode(String phone, String code) {
        Map<String, String> templateParam = new HashMap<>();
        templateParam.put("code", code);
        try {
            return sendSms(phone, smsConfig.getVerificationTemplateCode(), objectMapper.writeValueAsString(templateParam));
        } catch (Exception e) {
            log.error("验证码短信参数序列化失败", e);
            return false;
        }
    }

    @Override
    public boolean sendNotification(String phone, String templateParam) {
        return sendSms(phone, smsConfig.getNotificationTemplateCode(), templateParam);
    }

    @Override
    public boolean sendSms(String phone, String templateCode, String templateParam) {
        // 模拟模式：直接返回成功
        if (smsConfig.isMockMode()) {
            String mockRequestId = UUID.randomUUID().toString().replaceAll("-", "");
            log.info("【模拟模式】短信发送成功 - 手机号: {}, 模板: {}, 参数: {}, 模拟请求ID: {}", 
                    phone, templateCode, templateParam, mockRequestId);
            return true;
        }
        
        // 真实发送模式
        if (acsClient == null) {
            log.error("短信客户端未初始化，请检查配置");
            return false;
        }
        
        try {
            // 创建API请求并设置参数
            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(phone);
            request.setSignName(smsConfig.getSignName());
            request.setTemplateCode(templateCode);
            request.setTemplateParam(templateParam);

            // 发送短信
            SendSmsResponse response = acsClient.getAcsResponse(request);
            
            // 记录发送结果
            log.info("短信发送结果 - 手机号: {}, 模板: {}, 请求ID: {}, 响应: {}", 
                    phone, templateCode, response.getRequestId(), objectMapper.writeValueAsString(response));
            
            // 判断是否发送成功
            return "OK".equals(response.getCode());
            
        } catch (ServerException e) {
            log.error("短信发送服务端异常 - 手机号: {}, 错误: {}", phone, e.getMessage(), e);
            return false;
        } catch (ClientException e) {
            log.error("短信发送客户端异常 - 手机号: {}, 错误码: {}, 错误信息: {}, 请求ID: {}", 
                    phone, e.getErrCode(), e.getErrMsg(), e.getRequestId(), e);
            return false;
        } catch (Exception e) {
            log.error("短信发送未知异常 - 手机号: {}, 错误: {}", phone, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean isMockMode() {
        return smsConfig.isMockMode();
    }
} 