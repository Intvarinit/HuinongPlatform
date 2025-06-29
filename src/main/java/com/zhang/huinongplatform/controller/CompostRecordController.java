package com.zhang.huinongplatform.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.zhang.huinongplatform.common.Result;
import com.zhang.huinongplatform.entity.CompostRecord;
import com.zhang.huinongplatform.entity.dto.CompostCreateDTO;
import com.zhang.huinongplatform.entity.dto.CompostUpdateDTO;
import com.zhang.huinongplatform.entity.dto.CompostRateDTO;
import com.zhang.huinongplatform.service.CompostRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.zhang.huinongplatform.annotation.OperationLog;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "堆肥批次管理", description = "堆肥批次的创建、更新、查询、导出、统计等接口")
@RestController
@RequestMapping("/api/compost")
@RequiredArgsConstructor
public class CompostRecordController {
    private final CompostRecordService compostRecordService;

    @Operation(summary = "创建堆肥批次", description = "管理员/质检员创建堆肥批次")
    @SaCheckLogin
    @OperationLog("创建堆肥批次")
    @SaCheckRole({"admin", "inspector"})
    @PostMapping("/create")
    public Result<Void> create(@RequestBody CompostCreateDTO dto) {
        compostRecordService.createCompost(dto);
        return Result.success();
    }

    @Operation(summary = "更新堆肥批次", description = "管理员/质检员更新堆肥批次")
    @SaCheckLogin
    @OperationLog("更新堆肥批次")
    @SaCheckRole({"admin", "inspector"})
    @PostMapping("/update")
    public Result<Void> update(@RequestBody CompostUpdateDTO dto) {
        compostRecordService.updateCompost(dto);
        return Result.success();
    }

    @Operation(summary = "查询堆肥批次详情", description = "根据ID查询堆肥批次详情")
    @SaCheckLogin
    @GetMapping("/detail/{id}")
    public Result<CompostRecord> detail(@Parameter(description = "堆肥批次ID") @PathVariable Long id) {
        return Result.success(compostRecordService.getById(id));
    }

    @Operation(summary = "分页查询堆肥批次", description = "分页查询堆肥批次。支持按recoveryId和status筛选，不传recoveryId时查询所有批次（需管理员权限）")
    @SaCheckLogin
    @GetMapping("/page")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<CompostRecord>> page(
            @Parameter(description = "回收记录ID（可选）") @RequestParam(required = false) Long recoveryId,
            @Parameter(description = "状态（0进行中、1已完成、2异常、3已取消，可选）") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int size) {
        return Result.success(compostRecordService.pageCompost(recoveryId, status, page, size));
    }

    @Operation(summary = "完成堆肥批次", description = "管理员/质检员完成堆肥批次")
    @SaCheckLogin
    @OperationLog("完成堆肥批次")
    @SaCheckRole({"admin", "inspector"})
    @PostMapping("/finish/{id}")
    public Result<Void> finish(@Parameter(description = "堆肥批次ID") @PathVariable Long id,
                              @Parameter(description = "备注") @RequestParam(required = false) String remark) {
        compostRecordService.finishCompost(id, remark);
        return Result.success();
    }

    @Operation(summary = "异常标记堆肥批次", description = "管理员/质检员将堆肥批次标记为异常")
    @SaCheckLogin
    @OperationLog("异常标记堆肥批次")
    @SaCheckRole({"admin", "inspector"})
    @PostMapping("/abnormal/{id}")
    public Result<Void> abnormal(@Parameter(description = "堆肥批次ID") @PathVariable Long id,
                                @Parameter(description = "备注") @RequestParam(required = false) String remark) {
        compostRecordService.markAbnormal(id, remark);
        return Result.success();
    }

    @Operation(summary = "导出指定回收记录下堆肥批次为Excel", description = "导出某回收记录下的堆肥批次")
    @SaCheckLogin
    @GetMapping("/export")
    public void export(@Parameter(description = "回收记录ID") @RequestParam Long recoveryId,
                      javax.servlet.http.HttpServletResponse response) throws Exception {
        compostRecordService.exportByRecoveryIdExcel(recoveryId, response);
    }

    @Operation(summary = "导出全部堆肥批次Excel", description = "管理员/质检员导出全部堆肥批次")
    @SaCheckLogin
    @SaCheckRole({"admin", "inspector"})
    @GetMapping("/export/all")
    public void exportAll(javax.servlet.http.HttpServletResponse response) {
        compostRecordService.exportCompostExcel(response);
    }

    @Operation(summary = "异常率统计", description = "统计堆肥批次异常率")
    @SaCheckRole("admin")
    @GetMapping("/exception-rate")
    public Result<CompostRateDTO> getExceptionRate(
        @Parameter(description = "起始日期", example = "2024-06-01") @RequestParam String startDate,
        @Parameter(description = "结束日期", example = "2024-06-30") @RequestParam String endDate) {
        double rate = compostRecordService.getCompostExceptionRate(startDate, endDate);
        CompostRateDTO dto = new CompostRateDTO();
        dto.setRate(rate);
        return Result.success(dto);
    }

    @Operation(summary = "合格率统计", description = "统计堆肥批次合格率")
    @SaCheckRole("admin")
    @GetMapping("/pass-rate")
    public Result<CompostRateDTO> getPassRate(
        @Parameter(description = "起始日期", example = "2024-06-01") @RequestParam String startDate,
        @Parameter(description = "结束日期", example = "2024-06-30") @RequestParam String endDate) {
        double rate = compostRecordService.getCompostPassRate(startDate, endDate);
        CompostRateDTO dto = new CompostRateDTO();
        dto.setRate(rate);
        return Result.success(dto);
    }
} 