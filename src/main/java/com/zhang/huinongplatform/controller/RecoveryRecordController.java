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

import java.util.List;

@RestController
@RequestMapping("/api/recovery")
@RequiredArgsConstructor
public class RecoveryRecordController {
    private final RecoveryRecordService recoveryRecordService;

    // 农户提交回收申请
    @SaCheckLogin
    @OperationLog("提交回收申请")
    @PostMapping("/apply")
    public Result<Void> apply(@RequestBody RecoveryApplyDTO dto) {
        recoveryRecordService.applyRecovery(dto);
        return Result.success();
    }

    // 管理员/质检员审核回收申请
    @SaCheckLogin
    @SaCheckRole({"admin", "inspector"})
    @OperationLog("审核回收申请")
    @PostMapping("/audit")
    public Result<Void> audit(@RequestBody RecoveryAuditDTO dto) {
        recoveryRecordService.auditRecovery(dto);
        return Result.success();
    }

    // 农户查询自己的回收记录
    @SaCheckLogin
    @GetMapping("/my")
    public Result<Page<RecoveryRecord>> myRecords(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return Result.success(recoveryRecordService.pageMyRecords(page, size));
    }

    // 管理员/质检员查询所有回收记录
    @SaCheckLogin
    @SaCheckRole({"admin", "inspector"})
    @GetMapping("/all")
    public Result<Page<RecoveryRecord>> allRecords(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return Result.success(recoveryRecordService.pageAllRecords(page, size));
    }

    // 查询回收记录详情
    @SaCheckLogin
    @GetMapping("/detail/{id}")
    public Result<RecoveryRecord> detail(@PathVariable Long id) {
        return Result.success(recoveryRecordService.getById(id));
    }

    // 逻辑删除回收记录
    @SaCheckLogin
    @OperationLog("删除回收记录")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        recoveryRecordService.deleteRecovery(id);
        return Result.success();
    }

    // 回收量统计接口
    @SaCheckRole("admin")
    @GetMapping("/statistics")
    public Result<RecoveryStatisticsDTO> getRecoveryStatistics(@RequestParam String startDate,
                                                              @RequestParam String endDate,
                                                              @RequestParam(defaultValue = "day") String groupBy) {
        List<String> xAxis = recoveryRecordService.getDateRange(startDate, endDate, groupBy);
        List<Integer> data = recoveryRecordService.getRecoveryCountByDate(startDate, endDate, groupBy);
        RecoveryStatisticsDTO dto = new RecoveryStatisticsDTO();
        dto.setXAxis(xAxis);
        dto.setData(data);
        return Result.success(dto);
    }
} 