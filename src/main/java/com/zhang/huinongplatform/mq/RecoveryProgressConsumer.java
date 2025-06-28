package com.zhang.huinongplatform.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhang.huinongplatform.common.MessageUtils;
import com.zhang.huinongplatform.entity.RecoveryRecord;
import com.zhang.huinongplatform.entity.User;
import com.zhang.huinongplatform.mapper.RecoveryRecordMapper;
import com.zhang.huinongplatform.mapper.UserMapper;
import com.zhang.huinongplatform.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecoveryProgressConsumer {
    
    private final MessageService messageService;
    private final RecoveryRecordMapper recoveryRecordMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @RabbitListener(queues = "recovery_notification_queue")
    public void handleRecoveryProgress(Map<String, Object> message) {
        try {
            log.info("收到回收进度通知消息：{}", objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            log.info("收到回收进度通知消息：{}", message);
        }
        
        try {
            // 使用工具类安全地获取userId
            Long userId = MessageUtils.getLongValue(message, "userId");
            String progress = MessageUtils.getStringValue(message, "progress");
            
            if (userId != null && progress != null) {
                // 获取用户信息
                User user = userMapper.selectById(userId);
                if (user != null && user.getPhone() != null) {
                    // 获取回收记录信息（如果有recoveryId）
                    Long recoveryId = MessageUtils.getLongValue(message, "recoveryId");
                    String recoveryInfo = "";
                    if (recoveryId != null) {
                        RecoveryRecord recoveryRecord = recoveryRecordMapper.selectById(recoveryId);
                        if (recoveryRecord != null) {
                            recoveryInfo = recoveryRecord.getCropType() + " - " + recoveryRecord.getWeight() + "kg";
                        }
                    }
                    
                    // 构建模板参数
                    String templateParam = objectMapper.writeValueAsString(Map.of(
                        "progress", progress,
                        "recoveryInfo", recoveryInfo,
                        "type", "回收进度更新"
                    ));
                    
                    log.info("发送回收进度短信通知到消息队列 - 手机号: {}, 进度: {}", 
                            user.getPhone(), progress);
                    
                    // 发送短信通知
                    messageService.sendBusinessSmsNotification(user.getPhone(), templateParam);
                    log.info("回收进度短信通知发送到消息队列成功 - 手机号: {}, 进度: {}", 
                            user.getPhone(), progress);
                } else {
                    log.warn("未找到用户信息或用户手机号为空 - userId: {}", userId);
                }
            } else {
                log.warn("回收进度通知中userId或progress为空 - userId: {}, progress: {}", userId, progress);
            }
        } catch (Exception e) {
            log.error("处理回收进度通知异常 - 消息: {}, 错误: {}", message, e.getMessage(), e);
        }
    }
} 