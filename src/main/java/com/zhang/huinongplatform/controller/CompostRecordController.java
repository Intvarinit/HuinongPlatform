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

@RestController
@RequestMapping("/api/compost")
@RequiredArgsConstructor
public class CompostRecordController {
    private final CompostRecordService compostRecordService;

    // 创建堆肥批次（管理员/质检员）
    @SaCheckLogin
    @OperationLog("创建堆肥批次")
    @SaCheckRole({"admin", "inspector"})
    @PostMapping("/create")
    public Result<Void> create(@RequestBody CompostCreateDTO dto) {
        compostRecordService.createCompost(dto);
        return Result.success();
    }

    // 更新堆肥批次（管理员/质检员）
    @SaCheckLogin
    @OperationLog("更新堆肥批次")
    @SaCheckRole({"admin", "inspector"})
    @PostMapping("/update")
    public Result<Void> update(@RequestBody CompostUpdateDTO dto) {
        compostRecordService.updateCompost(dto);
        return Result.success();
    }

    // 查询堆肥批次详情
    @SaCheckLogin
    @GetMapping("/detail/{id}")
    public Result<CompostRecord> detail(@PathVariable Long id) {
        return Result.success(compostRecordService.getById(id));
    }

    // 分页查询某回收记录下的所有堆肥批次
    @SaCheckLogin
    @GetMapping("/page")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<CompostRecord>> page(
            @RequestParam Long recoveryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(compostRecordService.pageByRecoveryId(recoveryId, page, size));
    }

    // 完成堆肥批次（管理员/质检员）
    @SaCheckLogin
    @OperationLog("完成堆肥批次")
    @SaCheckRole({"admin", "inspector"})
    @PostMapping("/finish/{id}")
    public Result<Void> finish(@PathVariable Long id, @RequestParam(required = false) String remark) {
        compostRecordService.finishCompost(id, remark);
        return Result.success();
    }

    // 标记异常（管理员/质检员）
    @SaCheckLogin
    @OperationLog("异常标记堆肥批次")
    @SaCheckRole({"admin", "inspector"})
    @PostMapping("/abnormal/{id}")
    public Result<Void> abnormal(@PathVariable Long id, @RequestParam(required = false) String remark) {
        compostRecordService.markAbnormal(id, remark);
        return Result.success();
    }

    // 导出指定回收记录下堆肥批次为Excel
    @SaCheckLogin
    @GetMapping("/export")
    public void export(@RequestParam Long recoveryId, javax.servlet.http.HttpServletResponse response) throws Exception {
        compostRecordService.exportByRecoveryIdExcel(recoveryId, response);
    }

    // 导出全部堆肥批次Excel（管理员/质检员）
    @SaCheckLogin
    @SaCheckRole({"admin", "inspector"})
    @GetMapping("/export/all")
    public void exportAll(javax.servlet.http.HttpServletResponse response) {
        compostRecordService.exportCompostExcel(response);
    }

    // 异常率统计接口
    @SaCheckRole("admin")
    @GetMapping("/exception-rate")
    public Result<CompostRateDTO> getExceptionRate(@RequestParam String startDate,
                                                  @RequestParam String endDate) {
        double rate = compostRecordService.getCompostExceptionRate(startDate, endDate);
        CompostRateDTO dto = new CompostRateDTO();
        dto.setRate(rate);
        return Result.success(dto);
    }

    // 合格率统计接口
    @SaCheckRole("admin")
    @GetMapping("/pass-rate")
    public Result<CompostRateDTO> getPassRate(@RequestParam String startDate,
                                             @RequestParam String endDate) {
        double rate = compostRecordService.getCompostPassRate(startDate, endDate);
        CompostRateDTO dto = new CompostRateDTO();
        dto.setRate(rate);
        return Result.success(dto);
    }
} 