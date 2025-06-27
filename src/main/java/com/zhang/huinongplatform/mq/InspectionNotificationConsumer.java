package com.zhang.huinongplatform.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class InspectionNotificationConsumer {
    @RabbitListener(queues = "inspection_notification_queue")
    public void handleInspectionNotification(Map<String, Object> message) {
        if ("inspection_abnormal".equals(message.get("type"))) {
            System.out.println("收到抽检异常通知：" + message);
            // TODO: 可扩展为写入数据库、推送站内信等
        }
    }
} 