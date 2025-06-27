package com.zhang.huinongplatform.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class RecoveryProgressConsumer {
    @RabbitListener(queues = "recovery_notification_queue")
    public void handleRecoveryProgress(Map<String, Object> message) {
        System.out.println("收到回收进度通知：" + message);
        // TODO: 可扩展为推送、写入数据库等
    }
} 