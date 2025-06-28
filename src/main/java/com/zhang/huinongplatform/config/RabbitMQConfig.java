package com.zhang.huinongplatform.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String RECOVERY_EXCHANGE = "recovery_exchange";
    public static final String USER_EXCHANGE = "user_exchange";
    public static final String SMS_EXCHANGE = "sms_exchange";
    
    public static final String RECOVERY_NOTIFICATION_QUEUE = "recovery_notification_queue";
    public static final String USER_VERIFICATION_QUEUE = "user_verification_queue";
    public static final String DATA_SYNC_QUEUE = "data_sync_queue";
    public static final String INSPECTION_NOTIFICATION_QUEUE = "inspection_notification_queue";
    public static final String SMS_NOTIFICATION_QUEUE = "sms_notification_queue";
    
    public static final String RECOVERY_NOTIFICATION_ROUTING_KEY = "recovery.notification";
    public static final String USER_VERIFICATION_ROUTING_KEY = "user.verification";
    public static final String DATA_SYNC_ROUTING_KEY = "data.sync";
    public static final String SMS_NOTIFICATION_ROUTING_KEY = "sms.notification";
    
    // 回收进度通知交换机
    @Bean
    public FanoutExchange recoveryExchange() {
        return new FanoutExchange(RECOVERY_EXCHANGE);
    }
    
    // 用户验证交换机
    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(USER_EXCHANGE);
    }
    
    // 短信通知交换机
    @Bean
    public DirectExchange smsExchange() {
        return new DirectExchange(SMS_EXCHANGE);
    }
    
    // 回收通知队列
    @Bean
    public Queue recoveryNotificationQueue() {
        return new Queue(RECOVERY_NOTIFICATION_QUEUE);
    }
    
    // 用户验证队列
    @Bean
    public Queue userVerificationQueue() {
        return new Queue(USER_VERIFICATION_QUEUE);
    }
    
    // 短信通知队列
    @Bean
    public Queue smsNotificationQueue() {
        return new Queue(SMS_NOTIFICATION_QUEUE);
    }
    
    // 死信交换机
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange("dlx_exchange");
    }

    // 死信队列
    @Bean
    public Queue dlxQueue() {
        return new Queue("dlx_queue");
    }

    // 死信队列绑定
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with("dlx_routing_key");
    }

    // data_sync_queue 增加死信队列属性
    @Bean
    public Queue dataSyncQueue() {
        return QueueBuilder.durable(DATA_SYNC_QUEUE)
                .withArgument("x-dead-letter-exchange", "dlx_exchange")
                .withArgument("x-dead-letter-routing-key", "dlx_routing_key")
                .build();
    }
    
    // 抽检异常通知队列
    @Bean
    public Queue inspectionNotificationQueue() {
        return new Queue(INSPECTION_NOTIFICATION_QUEUE);
    }
    
    // 绑定回收通知队列到交换机
    @Bean
    public Binding bindingRecoveryNotification() {
        return BindingBuilder.bind(recoveryNotificationQueue())
                .to(recoveryExchange());
    }
    
    // 绑定用户验证队列到交换机
    @Bean
    public Binding bindingUserVerification() {
        return BindingBuilder.bind(userVerificationQueue())
                .to(userExchange())
                .with(USER_VERIFICATION_ROUTING_KEY);
    }
    
    // 绑定短信通知队列到交换机
    @Bean
    public Binding bindingSmsNotification() {
        return BindingBuilder.bind(smsNotificationQueue())
                .to(smsExchange())
                .with(SMS_NOTIFICATION_ROUTING_KEY);
    }
    
    // 绑定数据同步队列到交换机
    @Bean
    public Binding bindingDataSync() {
        return BindingBuilder.bind(dataSyncQueue())
                .to(userExchange())
                .with(DATA_SYNC_ROUTING_KEY);
    }
} 