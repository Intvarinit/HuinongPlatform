package com.zhang.huinongplatform.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.huinongplatform.common.Result;
import com.zhang.huinongplatform.entity.OperationLog;
import com.zhang.huinongplatform.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class OperationLogController {
    private final OperationLogService operationLogService;

    /**
     * 操作日志分页查询（仅管理员可查）
     */
    @SaCheckRole("admin")
    @GetMapping("/page")
    public Result<Page<OperationLog>> page(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String operation
    ) {
        return Result.success(operationLogService.pageLogs(page, size, username, operation));
    }

    /**
     * 删除操作日志（仅管理员可用）
     */
    @SaCheckRole("admin")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        operationLogService.removeById(id);
        return Result.success();
    }
} 