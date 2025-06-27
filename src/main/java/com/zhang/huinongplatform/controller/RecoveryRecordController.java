package com.zhang.huinongplatform.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.huinongplatform.common.Result;
import com.zhang.huinongplatform.entity.RecoveryRecord;
import com.zhang.huinongplatform.entity.dto.RecoveryApplyDTO;
import com.zhang.huinongplatform.entity.dto.RecoveryAuditDTO;
import com.zhang.huinongplatform.entity.dto.RecoveryStatisticsDTO;
import com.zhang.huinongplatform.service.RecoveryRecordService;
import com.zhang.huinongplatform.annotation.OperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@Tag(name = "回收登记管理", description = "回收申请、审核、查询、统计等接口")
@RestController
@RequestMapping("/api/recovery")
@RequiredArgsConstructor
public class RecoveryRecordController {
    private final RecoveryRecordService recoveryRecordService;

    @Operation(summary = "提交回收申请", description = "农户提交回收申请，需登录。图片请先通过 /api/common/upload/image 上传，images 字段为图片URL数组（如 ['/upload/xxx.png']）")
    @SaCheckLogin
    @OperationLog("提交回收申请")
    @PostMapping("/apply")
    public Result<Void> apply(@RequestBody RecoveryApplyDTO dto) {
        recoveryRecordService.applyRecovery(dto);
        return Result.success();
    }

    @Operation(summary = "审核回收申请", description = "管理员/质检员审核回收申请")
    @SaCheckLogin
    @SaCheckRole({"admin", "inspector"})
    @OperationLog("审核回收申请")
    @PostMapping("/audit")
    public Result<Void> audit(@RequestBody RecoveryAuditDTO dto) {
        recoveryRecordService.auditRecovery(dto);
        return Result.success();
    }

    @Operation(summary = "分页查询我的回收记录", description = "农户分页查询自己的回收记录")
    @SaCheckLogin
    @GetMapping("/my")
    public Result<Page<RecoveryRecord>> myRecords(
        @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int size) {
        return Result.success(recoveryRecordService.pageMyRecords(page, size));
    }

    @Operation(summary = "分页查询所有回收记录", description = "管理员/质检员分页查询所有回收记录")
    @SaCheckLogin
    @SaCheckRole({"admin", "inspector"})
    @GetMapping("/all")
    public Result<Page<RecoveryRecord>> allRecords(
        @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int size) {
        return Result.success(recoveryRecordService.pageAllRecords(page, size));
    }

    @Operation(summary = "查询回收记录详情", description = "根据ID查询回收记录详情")
    @SaCheckLogin
    @GetMapping("/detail/{id}")
    public Result<RecoveryRecord> detail(@Parameter(description = "回收记录ID") @PathVariable Long id) {
        return Result.success(recoveryRecordService.getById(id));
    }

    @Operation(summary = "删除回收记录", description = "逻辑删除回收记录")
    @SaCheckLogin
    @OperationLog("删除回收记录")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@Parameter(description = "回收记录ID") @PathVariable Long id) {
        recoveryRecordService.deleteRecovery(id);
        return Result.success();
    }

    @Operation(summary = "回收量统计", description = "按时间区间统计回收量，支持按天/月/年分组")
    @SaCheckRole("admin")
    @GetMapping("/statistics")
    public Result<RecoveryStatisticsDTO> getRecoveryStatistics(
        @Parameter(description = "起始日期", example = "2024-06-01") @RequestParam String startDate,
        @Parameter(description = "结束日期", example = "2024-06-30") @RequestParam String endDate,
        @Parameter(description = "分组方式(day/month/year)", example = "day") @RequestParam(defaultValue = "day") String groupBy) {
        List<String> xAxis = recoveryRecordService.getDateRange(startDate, endDate, groupBy);
        List<Integer> data = recoveryRecordService.getRecoveryCountByDate(startDate, endDate, groupBy);
        RecoveryStatisticsDTO dto = new RecoveryStatisticsDTO();
        dto.setXAxis(xAxis);
        dto.setData(data);
        return Result.success(dto);
    }
} 