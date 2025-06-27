package com.zhang.huinongplatform.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.huinongplatform.common.Result;
import com.zhang.huinongplatform.entity.InspectionRecord;
import com.zhang.huinongplatform.entity.dto.InspectionCreateDTO;
import com.zhang.huinongplatform.service.InspectionRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/inspection")
@RequiredArgsConstructor
public class InspectionRecordController {
    private final InspectionRecordService inspectionRecordService;

    // 新增抽检记录（仅管理员/质检员）
    @SaCheckLogin
    @SaCheckRole({"admin", "inspector"})
    @PostMapping("/create")
    public Result<Void> create(@RequestBody InspectionCreateDTO dto) {
        inspectionRecordService.createInspection(dto);
        return Result.success();
    }

    // 分页查询抽检记录
    @SaCheckLogin
    @GetMapping("/page")
    public Result<Page<InspectionRecord>> page(@RequestParam Long compostId,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        if (cn.dev33.satoken.stp.StpUtil.hasRole("admin") || cn.dev33.satoken.stp.StpUtil.hasRole("inspector")) {
            return Result.success(inspectionRecordService.pageInspection(compostId, page, size));
        } else {
            if (!inspectionRecordService.isCompostBelongToUser(compostId, cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong())) {
                throw new com.zhang.huinongplatform.common.BizException("无权查看该抽检记录");
            }
            return Result.success(inspectionRecordService.pageInspection(compostId, page, size));
        }
    }

    // 查询抽检详情
    @SaCheckLogin
    @GetMapping("/detail/{id}")
    public Result<InspectionRecord> detail(@PathVariable Long id) {
        InspectionRecord record = inspectionRecordService.getById(id);
        if (cn.dev33.satoken.stp.StpUtil.hasRole("admin") || cn.dev33.satoken.stp.StpUtil.hasRole("inspector")) {
            return Result.success(record);
        } else {
            if (!inspectionRecordService.isCompostBelongToUser(record.getCompostId(), cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong())) {
                throw new com.zhang.huinongplatform.common.BizException("无权查看该抽检记录");
            }
            return Result.success(record);
        }
    }

    // 导出抽检记录Excel（仅管理员/质检员）
    @SaCheckLogin
    @SaCheckRole({"admin", "inspector"})
    @GetMapping("/export")
    public void export(@RequestParam Long compostId, HttpServletResponse response) throws Exception {
        inspectionRecordService.exportInspectionExcel(compostId, response);
    }
} 