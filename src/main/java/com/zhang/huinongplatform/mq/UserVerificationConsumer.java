package com.zhang.huinongplatform.mq;

import com.zhang.huinongplatform.common.MessageUtils;
import com.zhang.huinongplatform.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserVerificationConsumer {
    
    private final MessageService messageService;
    
    @RabbitListener(queues = "user_verification_queue")
    public void handleUserVerification(Map<String, Object> message) {
        String phone = MessageUtils.getStringValue(message, "phone");
        String code = MessageUtils.getStringValue(message, "code");
        
        try {
            log.info("收到用户验证码消息 - 手机号: {}, 验证码: {}", phone, code);
            
            if (phone != null && code != null) {
                // 通过消息队列发送验证码短信
                messageService.sendVerificationSmsNotification(phone, code);
            } else {
                log.warn("验证码消息中手机号或验证码为空 - phone: {}, code: {}", phone, code);
            }
            
        } catch (Exception e) {
            log.error("处理用户验证码消息异常 - 手机号: {}, 验证码: {}, 错误: {}", phone, code, e.getMessage(), e);
        }
    }
} 