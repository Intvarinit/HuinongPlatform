package com.zhang.huinongplatform.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhang.huinongplatform.config.AliyunSmsConfig;
import com.zhang.huinongplatform.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsNotificationConsumer {
    
    private final SmsService smsService;
    private final AliyunSmsConfig smsConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @RabbitListener(queues = "sms_notification_queue")
    public void handleSmsNotification(Map<String, Object> message) {
        try {
            String type = (String) message.get("type");
            String phone = (String) message.get("phone");
            
            log.info("收到短信通知消息 - 类型: {}, 手机号: {}, 消息: {}", type, phone, message);
            
            switch (type) {
                case "verification_sms":
                    handleVerificationSms(message);
                    break;
                case "business_sms":
                    handleBusinessSms(message);
                    break;
                case "sms_notification":
                    handleCustomSms(message);
                    break;
                default:
                    log.warn("未知的短信通知类型: {}", type);
            }
            
        } catch (Exception e) {
            log.error("处理短信通知消息异常 - 消息: {}, 错误: {}", message, e.getMessage(), e);
        }
    }
    
    private void handleVerificationSms(Map<String, Object> message) {
        String phone = (String) message.get("phone");
        String code = (String) message.get("code");
        
        try {
            boolean success = smsService.sendVerificationCode(phone, code);
            if (success) {
                log.info("验证码短信发送成功 - 手机号: {}, 验证码: {}", phone, code);
            } else {
                log.error("验证码短信发送失败 - 手机号: {}, 验证码: {}", phone, code);
            }
        } catch (Exception e) {
            log.error("验证码短信发送异常 - 手机号: {}, 验证码: {}, 错误: {}", phone, code, e.getMessage(), e);
        }
    }
    
    private void handleBusinessSms(Map<String, Object> message) {
        String phone = (String) message.get("phone");
        String templateParam = (String) message.get("templateParam");
        
        try {
            boolean success = smsService.sendNotification(phone, templateParam);
            if (success) {
                log.info("业务短信发送成功 - 手机号: {}, 参数: {}", phone, templateParam);
            } else {
                log.error("业务短信发送失败 - 手机号: {}, 参数: {}", phone, templateParam);
            }
        } catch (Exception e) {
            log.error("业务短信发送异常 - 手机号: {}, 参数: {}, 错误: {}", phone, templateParam, e.getMessage(), e);
        }
    }
    
    private void handleCustomSms(Map<String, Object> message) {
        String phone = (String) message.get("phone");
        String templateCode = (String) message.get("templateCode");
        String templateParam = (String) message.get("templateParam");
        
        try {
            boolean success = smsService.sendSms(phone, templateCode, templateParam);
            if (success) {
                log.info("自定义短信发送成功 - 手机号: {}, 模板: {}, 参数: {}", phone, templateCode, templateParam);
            } else {
                log.error("自定义短信发送失败 - 手机号: {}, 模板: {}, 参数: {}", phone, templateCode, templateParam);
            }
        } catch (Exception e) {
            log.error("自定义短信发送异常 - 手机号: {}, 模板: {}, 参数: {}, 错误: {}", 
                    phone, templateCode, templateParam, e.getMessage(), e);
        }
    }
} 