package com.zhang.huinongplatform.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.zhang.huinongplatform.common.Result;
import com.zhang.huinongplatform.entity.CompostProgressLog;
import com.zhang.huinongplatform.entity.dto.CompostProgressLogDTO;
import com.zhang.huinongplatform.service.CompostProgressLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compost/progress-log")
@RequiredArgsConstructor
public class CompostProgressLogController {
    private final CompostProgressLogService compostProgressLogService;

    // 添加进度日志
    @SaCheckLogin
    @PostMapping("/add")
    public Result<Void> add(@RequestBody CompostProgressLogDTO dto) {
        compostProgressLogService.addProgressLog(dto);
        return Result.success();
    }

    // 分页查询某批次的进度日志
    @SaCheckLogin
    @GetMapping("/list")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<CompostProgressLog>> list(@RequestParam Long compostId,
                                                                                                      @RequestParam(defaultValue = "1") int page,
                                                                                                      @RequestParam(defaultValue = "10") int size) {
        return Result.success(compostProgressLogService.pageByCompostId(compostId, page, size));
    }
} 