package com.zhang.huinongplatform.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.zhang.huinongplatform.common.Result;
import com.zhang.huinongplatform.entity.CompostProgressLog;
import com.zhang.huinongplatform.entity.dto.CompostProgressLogDTO;
import com.zhang.huinongplatform.service.CompostProgressLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "堆肥进度日志管理", description = "堆肥批次进度日志的添加、查询等接口")
@RestController
@RequestMapping("/api/compost/progress-log")
@RequiredArgsConstructor
public class CompostProgressLogController {
    private final CompostProgressLogService compostProgressLogService;

    @Operation(summary = "添加进度日志", description = "为堆肥批次添加进度日志")
    @SaCheckLogin
    @PostMapping("/add")
    public Result<Void> add(@RequestBody CompostProgressLogDTO dto) {
        compostProgressLogService.addProgressLog(dto);
        return Result.success();
    }

    @Operation(summary = "分页查询进度日志", description = "分页查询某批次的进度日志")
    @SaCheckLogin
    @GetMapping("/list")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<CompostProgressLog>> list(
        @Parameter(description = "堆肥批次ID") @RequestParam Long compostId,
        @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int size) {
        if (compostId == null || compostId <= 0) {
            throw new com.zhang.huinongplatform.common.BizException("堆肥批次ID不能为空且必须大于0");
        }
        return Result.success(compostProgressLogService.pageByCompostId(compostId, page, size));
    }
} 