package com.zhang.huinongplatform.mq;


import com.zhang.huinongplatform.service.CompostRecordService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class CompostMessageConsumer {

    @Autowired
    private CompostRecordService compostRecordService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RabbitListener(queues = "data_sync_queue")     //堆肥异常会发送到这里
    public void handleDataSyncMessage(Map<String, Object> message) {
        String msgId = (String) message.get("msgId");
        if (msgId != null && Boolean.TRUE.equals(redisTemplate.hasKey("mq:msg:" + msgId))) {
            System.out.println("[幂等] 已消费过消息: " + msgId);
            return;
        }
        System.out.println("收到堆肥批次通知消息：" + message);
        // 业务处理...（如写入日志、数据库、推送等）
        if (msgId != null) {
            redisTemplate.opsForValue().set("mq:msg:" + msgId, "1", 1, TimeUnit.DAYS);
        }
    }
} 