package com.zhang.huinongplatform.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhang.huinongplatform.common.MessageUtils;
import com.zhang.huinongplatform.entity.CompostRecord;
import com.zhang.huinongplatform.entity.RecoveryRecord;
import com.zhang.huinongplatform.entity.User;
import com.zhang.huinongplatform.mapper.CompostRecordMapper;
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
public class InspectionNotificationConsumer {
    
    private final MessageService messageService;
    private final CompostRecordMapper compostRecordMapper;
    private final RecoveryRecordMapper recoveryRecordMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @RabbitListener(queues = "inspection_notification_queue")
    public void handleInspectionNotification(Map<String, Object> message) {
        try {
            log.info("收到抽检通知消息：{}", objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            log.info("收到抽检通知消息：{}", message);
        }
        
        try {
            // 检查是否为异常通知
            boolean isAbnormal = "inspection_abnormal".equals(message.get("type")) || 
                                (message.get("result") != null && Integer.valueOf(1).equals(message.get("result")));
            
            // 使用工具类安全地获取compostId
            Long compostId = MessageUtils.getLongValue(message, "compostId");
            
            if (compostId != null) {
                // 获取堆肥记录
                CompostRecord compostRecord = compostRecordMapper.selectById(compostId);
                if (compostRecord != null) {
                    // 获取回收记录
                    RecoveryRecord recoveryRecord = recoveryRecordMapper.selectById(compostRecord.getRecoveryId());
                    if (recoveryRecord != null) {
                        // 获取用户信息
                        User user = userMapper.selectById(recoveryRecord.getUserId());
                        if (user != null && user.getPhone() != null) {
                            // 构建模板参数
                            String templateParam;
                            if (isAbnormal) {
                                // 异常通知
                                templateParam = objectMapper.writeValueAsString(Map.of(
                                    "batchNo", compostRecord.getBatchNo(),
                                    "reason", MessageUtils.getStringValue(message, "remark"),
                                    "type", "抽检异常"
                                ));
                                log.info("发送抽检异常短信通知 - 手机号: {}, 批次号: {}, 原因: {}", 
                                        user.getPhone(), compostRecord.getBatchNo(), message.get("remark"));
                            } else {
                                // 普通抽检通知
                                templateParam = objectMapper.writeValueAsString(Map.of(
                                    "batchNo", compostRecord.getBatchNo(),
                                    "remark", MessageUtils.getStringValue(message, "remark"),
                                    "phValue", MessageUtils.getDoubleValue(message, "phValue"),
                                    "temperature", MessageUtils.getDoubleValue(message, "temperature"),
                                    "humidity", MessageUtils.getDoubleValue(message, "humidity"),
                                    "type", "抽检完成"
                                ));
                                log.info("发送抽检完成短信通知到消息队列 - 手机号: {}, 批次号: {}",
                                        user.getPhone(), compostRecord.getBatchNo());
                            }
                            
                            // 发送短信通知
                            messageService.sendBusinessSmsNotification(user.getPhone(), templateParam);
                            log.info("抽检短信通知发送到消息队列成功 - 手机号: {}, 批次号: {}",
                                    user.getPhone(), compostRecord.getBatchNo());
                        } else {
                            log.warn("未找到用户信息或用户手机号为空 - compostId: {}, userId: {}", 
                                    compostId, recoveryRecord.getUserId());
                        }
                    } else {
                        log.warn("未找到回收记录 - compostId: {}, recoveryId: {}", 
                                compostId, compostRecord.getRecoveryId());
                    }
                } else {
                    log.warn("未找到堆肥记录 - compostId: {}", compostId);
                }
            } else {
                log.warn("抽检通知中compostId为空或类型不支持");
            }
        } catch (Exception e) {
            log.error("处理抽检通知异常 - 消息: {}, 错误: {}", message, e.getMessage(), e);
        }
    }
} 