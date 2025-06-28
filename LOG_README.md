# 日志配置说明

## 📋 日志级别说明

修改到INFO级别后，不同级别的日志会输出到以下位置：

### 🖥️ 控制台输出
- **所有级别**（INFO、WARN、ERROR）的日志都会显示在控制台
- 格式：`2024-01-01 12:00:00 [thread] INFO com.zhang.huinongplatform.xxx - 日志信息`

### 📁 日志文件分布

#### 1. **普通日志文件**
- **文件路径**: `logs/huinong-platform.log`
- **包含级别**: INFO、WARN、ERROR
- **内容**: 所有业务日志信息

#### 2. **错误日志文件**
- **文件路径**: `logs/huinong-platform-error.log`
- **包含级别**: 仅ERROR级别
- **内容**: 只包含错误和异常信息

#### 3. **短信服务专用日志**
- **文件路径**: `logs/sms-service.log`
- **包含级别**: INFO、WARN、ERROR
- **内容**: 短信相关的所有日志

#### 4. **消息队列专用日志**
- **文件路径**: `logs/message-queue.log`
- **包含级别**: INFO、WARN、ERROR
- **内容**: 消息队列相关的所有日志

## 🔍 日志查看方法

### 1. **实时查看控制台日志**
```bash
# 启动应用时查看控制台输出
java -jar huinong-platform.jar

# 或者在IDE中直接查看控制台
```

### 2. **查看日志文件**
```bash
# 查看普通日志
tail -f logs/huinong-platform.log

# 查看错误日志
tail -f logs/huinong-platform-error.log

# 查看短信服务日志
tail -f logs/sms-service.log

# 查看消息队列日志
tail -f logs/message-queue.log
```

### 3. **搜索特定日志**
```bash
# 搜索验证码相关日志
grep "验证码" logs/huinong-platform.log

# 搜索错误日志
grep "ERROR" logs/huinong-platform-error.log

# 搜索短信发送日志
grep "短信发送" logs/sms-service.log
```

## 📊 日志示例

### INFO级别日志示例
```
2024-01-01 12:00:00 [http-nio-8080-exec-1] INFO  c.z.h.service.impl.VerificationCodeServiceImpl - 登录验证码发送成功 - 手机号: 13800138000, 验证码: 123456, 过期时间: 5分钟
2024-01-01 12:00:01 [http-nio-8080-exec-2] INFO  c.z.h.mq.SmsNotificationConsumer - 收到短信通知消息 - 类型: verification_sms, 手机号: 13800138000
2024-01-01 12:00:02 [http-nio-8080-exec-3] INFO  c.z.h.service.impl.SmsServiceImpl - 验证码短信发送成功 - 手机号: 13800138000, 验证码: 123456
```

### ERROR级别日志示例
```
2024-01-01 12:00:00 [http-nio-8080-exec-1] ERROR c.z.h.service.impl.SmsServiceImpl - 短信发送失败 - 手机号: 13800138000, 错误: 网络连接超时
2024-01-01 12:00:01 [http-nio-8080-exec-2] ERROR c.z.h.mq.SmsNotificationConsumer - 处理短信通知消息异常 - 手机号: 13800138000, 错误: 模板参数格式错误
```

## ⚙️ 日志配置说明

### 1. **application.yml配置**
```yaml
logging:
  level:
    root: info  # 根日志级别
    com.zhang.huinongplatform: info  # 项目包的日志级别
  config: classpath:logback-spring.xml  # 使用自定义logback配置
```

### 2. **logback-spring.xml配置**
- **控制台输出**: 所有级别日志
- **文件输出**: 按级别和模块分离
- **滚动策略**: 按日期和大小滚动
- **保留策略**: 保留30天，单个文件最大10MB

## 🔧 日志级别调整

### 临时调整日志级别
```yaml
logging:
  level:
    com.zhang.huinongplatform.service: debug  # 调整为debug级别
    com.zhang.huinongplatform.mq: warn        # 调整为warn级别
```

### 生产环境建议
```yaml
logging:
  level:
    root: warn  # 生产环境建议使用warn级别
    com.zhang.huinongplatform: info  # 项目包保持info级别
```

## 📈 日志监控

### 1. **关键日志监控点**
- 验证码发送成功/失败
- 短信发送成功/失败
- 消息队列处理状态
- 系统异常和错误

### 2. **日志分析工具**
- **ELK Stack**: Elasticsearch + Logstash + Kibana
- **Grafana**: 日志可视化
- **Prometheus**: 日志指标收集

### 3. **告警配置**
- 错误日志数量告警
- 短信发送失败率告警
- 消息队列积压告警

## 🚨 常见问题

### 1. **日志文件过大**
- 检查滚动策略配置
- 调整日志级别
- 清理历史日志文件

### 2. **日志输出混乱**
- 检查日志格式配置
- 确认字符编码设置
- 验证日志级别配置

### 3. **性能影响**
- 生产环境使用异步日志
- 调整日志缓冲区大小
- 优化日志输出频率

## 📝 最佳实践

1. **合理使用日志级别**
   - DEBUG: 调试信息
   - INFO: 重要业务信息
   - WARN: 警告信息
   - ERROR: 错误信息

2. **日志内容规范**
   - 包含关键业务参数
   - 使用统一的日志格式
   - 避免敏感信息泄露

3. **日志文件管理**
   - 定期清理历史日志
   - 监控日志文件大小
   - 配置日志备份策略 