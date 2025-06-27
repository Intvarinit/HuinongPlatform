package com.zhang.huinongplatform.aop;

import com.zhang.huinongplatform.entity.OperationLog;
import com.zhang.huinongplatform.entity.User;
import com.zhang.huinongplatform.service.OperationLogService;
import com.zhang.huinongplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import cn.dev33.satoken.stp.StpUtil;
import java.util.Date;
import java.lang.reflect.Method;
import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    private final OperationLogService operationLogService;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(com.zhang.huinongplatform.annotation.OperationLog)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method methodObj = signature.getMethod();
        // 反射获取实现类方法上的注解
        Method realMethod = joinPoint.getTarget().getClass()
                .getMethod(signature.getName(), methodObj.getParameterTypes());
        com.zhang.huinongplatform.annotation.OperationLog annotation =
                realMethod.getAnnotation(com.zhang.huinongplatform.annotation.OperationLog.class);
        String operation = annotation != null ? annotation.value() : "";
        String method = signature.getDeclaringTypeName() + "." + signature.getName();
        String params = "";
        if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
            try {
                params = objectMapper.writeValueAsString(joinPoint.getArgs());
            } catch (Exception ignored) {}
        }
        String ip = "";
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            if (request != null) {
                ip = request.getRemoteAddr();
            }
        }
        Long userId = null;
        String username = null;
        try {
            userId = StpUtil.getLoginIdAsLong();
            User user = userService.getCurrentUser();
            if (user != null) {
                username = user.getUsername();
            }
        } catch (Exception ignored) {}
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation(operation);
        log.setMethod(method);
        log.setParams(params);
        log.setIp(ip);
        log.setCreateTime(new Date());
        Object result = null;
        try {
            result = joinPoint.proceed();
            log.setResult("成功");
        } catch (Throwable e) {
            log.setResult("失败");
            log.setErrorMsg(e.getMessage());
            operationLogService.saveLog(log);
            throw e;
        }
        operationLogService.saveLog(log);
        return result;
    }
} 