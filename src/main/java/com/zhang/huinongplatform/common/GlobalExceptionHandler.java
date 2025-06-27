package com.zhang.huinongplatform.common;

import cn.dev33.satoken.exception.NotLoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //日志记录
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLoginException(NotLoginException e) {
        // 401 未登录
        return Result.error(401, "未登录或登录已失效");
    }

    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        // 业务异常只记录警告
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        // 记录详细错误日志
        log.error("系统异常", e);
        // 返回友好提示
        return Result.error(500, "服务器内部错误");
    }
}