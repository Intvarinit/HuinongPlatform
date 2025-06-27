package com.zhang.huinongplatform.service.impl;

import com.zhang.huinongplatform.config.RabbitMQConfig;
import com.zhang.huinongplatform.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
    }

    @Override
    public void sendVerificationCode(String phone, String code) {
        Map<String, Object> message = new HashMap<>();
        message.put("phone", phone);
        message.put("code", code);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.USER_EXCHANGE,
            RabbitMQConfig.USER_VERIFICATION_ROUTING_KEY,
            message
        );
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
    }
} 