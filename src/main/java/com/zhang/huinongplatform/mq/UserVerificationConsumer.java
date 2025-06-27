package com.zhang.huinongplatform.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class UserVerificationConsumer {
    @RabbitListener(queues = "user_verification_queue")
    public void handleUserVerification(Map<String, Object> message) {
        String phone = (String) message.get("phone");
        String code = (String) message.get("code");
        // TODO: 调用短信服务发送验证码，如阿里云短信
        System.out.println("发送验证码到 " + phone + ", 验证码: " + code);
    }
} 