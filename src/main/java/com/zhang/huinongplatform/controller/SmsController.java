package com.zhang.huinongplatform.controller;

import com.zhang.huinongplatform.common.Result;
import com.zhang.huinongplatform.entity.dto.SendCodeDTO;
import com.zhang.huinongplatform.service.MessageService;
import com.zhang.huinongplatform.service.SmsService;
import com.zhang.huinongplatform.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;
    private final MessageService messageService;
    private final VerificationCodeService verificationCodeService;

    /**
     * 发送登录验证码
     */
    @PostMapping("/sendLoginCode")
    public Result<String> sendLoginCode(@RequestBody SendCodeDTO dto) {
        try {
            // 通过消息队列发送验证码
            messageService.sendVerificationSmsNotification(dto.getPhone(), "123456");
            return Result.success("验证码发送成功");
        } catch (Exception e) {
            log.error("发送登录验证码异常", e);
            return Result.error("验证码发送失败");
        }
    }

    /**
     * 发送自定义短信
     */
    @PostMapping("/sendCustomSms")
    public Result<String> sendCustomSms(@RequestBody Map<String, String> request) {
        try {
            String phone = request.get("phone");
            String templateCode = request.get("templateCode");
            String templateParam = request.get("templateParam");
            
            // 通过消息队列发送自定义短信
            messageService.sendSmsNotification(phone, templateCode, templateParam);
            return Result.success("短信发送成功");
        } catch (Exception e) {
            log.error("发送自定义短信异常", e);
            return Result.error("短信发送失败");
        }
    }

    /**
     * 发送业务通知短信
     */
    @PostMapping("/sendBusinessSms")
    public Result<String> sendBusinessSms(@RequestBody Map<String, String> request) {
        try {
            String phone = request.get("phone");
            String templateParam = request.get("templateParam");
            
            // 通过消息队列发送业务短信
            messageService.sendBusinessSmsNotification(phone, templateParam);
            return Result.success("业务短信发送成功");
        } catch (Exception e) {
            log.error("发送业务短信异常", e);
            return Result.error("业务短信发送失败");
        }
    }

    /**
     * 查看验证码（仅用于测试）
     */
    @GetMapping("/viewCode/{phone}")
    public Result<String> viewCode(@PathVariable String phone) {
        try {
            String code = verificationCodeService.getStoredCode(phone);
            return Result.success(code != null ? code : "验证码不存在或已过期");
        } catch (Exception e) {
            log.error("查看验证码异常", e);
            return Result.error("查看验证码失败");
        }
    }

    /**
     * 检查短信模式
     */
    @GetMapping("/checkMode")
    public Result<Map<String, Object>> checkMode() {
        try {
            boolean isSimulationMode = smsService.isMockMode();
            return Result.success(Map.of(
                "simulationMode", isSimulationMode,
                "message", isSimulationMode ? "当前为模拟模式，短信不会真实发送" : "当前为真实模式，短信将真实发送"
            ));
        } catch (Exception e) {
            log.error("检查短信模式异常", e);
            return Result.error("检查短信模式失败");
        }
    }

    /**
     * 测试短信发送
     */
    @PostMapping("/test")
    public Result<String> testSms(@RequestBody Map<String, String> request) {
        try {
            String phone = request.get("phone");
            String message = request.get("message");
            
            // 通过消息队列发送测试短信
            messageService.sendBusinessSmsNotification(phone, "{\"message\":\"" + message + "\"}");
            return Result.success("测试短信发送成功");
        } catch (Exception e) {
            log.error("测试短信发送异常", e);
            return Result.error("测试短信发送失败");
        }
    }
} 