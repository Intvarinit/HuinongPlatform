package com.zhang.huinongplatform.service.impl;

import com.zhang.huinongplatform.config.RabbitMQConfig;
import com.zhang.huinongplatform.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendRecoveryProgressUpdate(Long userId, String progress) {
        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("progress", progress);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.RECOVERY_EXCHANGE,
            RabbitMQConfig.RECOVERY_NOTIFICATION_ROUTING_KEY,
            message
        );
        log.info("发送回收进度更新消息 - 用户ID: {}, 进度: {}", userId, progress);
    }

    @Override
    public void sendRecoveryProgressUpdate(Long userId, Long recoveryId, String progress) {
        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("recoveryId", recoveryId);
        message.put("progress", progress);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.RECOVERY_EXCHANGE,
            RabbitMQConfig.RECOVERY_NOTIFICATION_ROUTING_KEY,
            message
        );
        log.info("发送回收进度更新消息 - 用户ID: {}, 回收记录ID: {}, 进度: {}", userId, recoveryId, progress);
    }

    @Override
    public void sendVerificationCode(String phone, String code) {
        // 发送到消息队列
        Map<String, Object> message = new HashMap<>();
        message.put("phone", phone);
        message.put("code", code);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.USER_EXCHANGE,
            RabbitMQConfig.USER_VERIFICATION_ROUTING_KEY,
            message
        );
        log.info("发送验证码消息到队列 - 手机号: {}, 验证码: {}", phone, code);
    }

    @Override
    public void sendDataSync(Object data) {
        Map<String, Object> map;
        if (data instanceof Map) {
            map = new HashMap<>();
            ((Map<?, ?>) data).forEach((k, v) -> {
                if (v instanceof java.time.LocalDateTime) {
                    map.put(String.valueOf(k), v.toString());
                } else {
                    map.put(String.valueOf(k), v);
                }
            });
        } else {
            throw new IllegalArgumentException("sendDataSync只支持Map类型");
        }
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.USER_EXCHANGE,
            RabbitMQConfig.DATA_SYNC_ROUTING_KEY,
            map
        );
        log.info("发送数据同步消息 - 数据: {}", map);
    }

    @Override
    public void sendInspectionNotification(Map<String, Object> message) {
        Map<String, Object> map = new HashMap<>();
        message.forEach((k, v) -> {
            if (v instanceof java.time.LocalDateTime) {
                map.put(String.valueOf(k), v.toString());
            } else {
                map.put(String.valueOf(k), v);
            }
        });
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.INSPECTION_NOTIFICATION_QUEUE,
            map
        );
        log.info("发送抽检通知消息 - 消息: {}", map);
    }

    @Override
    public void sendSmsNotification(String phone, String templateCode, String templateParam) {
        Map<String, Object> message = new HashMap<>();
        message.put("phone", phone);
        message.put("templateCode", templateCode);
        message.put("templateParam", templateParam);
        message.put("type", "sms_notification");
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.SMS_EXCHANGE,
            RabbitMQConfig.SMS_NOTIFICATION_ROUTING_KEY,
            message
        );
        log.info("发送短信通知消息到队列 - 手机号: {}, 模板: {}, 参数: {}", phone, templateCode, templateParam);
    }

    @Override
    public void sendVerificationSmsNotification(String phone, String code) {
        Map<String, Object> message = new HashMap<>();
        message.put("phone", phone);
        message.put("code", code);
        message.put("type", "verification_sms");
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.SMS_EXCHANGE,
            RabbitMQConfig.SMS_NOTIFICATION_ROUTING_KEY,
            message
        );
        log.info("发送验证码短信通知消息到队列 - 手机号: {}, 验证码: {}", phone, code);
    }

    @Override
    public void sendBusinessSmsNotification(String phone, String templateParam) {
        Map<String, Object> message = new HashMap<>();
        message.put("phone", phone);
        message.put("templateParam", templateParam);
        message.put("type", "business_sms");
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.SMS_EXCHANGE,
            RabbitMQConfig.SMS_NOTIFICATION_ROUTING_KEY,
            message
        );
        log.info("发送业务短信通知消息到队列 - 手机号: {}, 参数: {}", phone, templateParam);
    }
} 