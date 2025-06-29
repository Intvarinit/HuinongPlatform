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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "抽检管理", description = "抽检记录的创建、查询、导出等接口")
@RestController
@RequestMapping("/api/inspection")
@RequiredArgsConstructor
public class InspectionRecordController {
    private final InspectionRecordService inspectionRecordService;

    @Operation(summary = "新增抽检记录", description = "管理员/质检员新增抽检记录。图片请先通过 /api/common/upload/image 上传，images 字段为图片URL数组（如 ['/upload/xxx.png']）")
    @SaCheckLogin
    @SaCheckRole({"admin", "inspector"})
    @PostMapping("/create")
    public Result<Void> create(@RequestBody InspectionCreateDTO dto) {
        inspectionRecordService.createInspection(dto);
        return Result.success();
    }

    @Operation(summary = "分页查询抽检记录", description = "分页查询抽检记录。不传compostId时查询所有记录（需管理员权限），传compostId时查询指定堆肥批次下的记录")
    @SaCheckLogin
    @GetMapping("/page")
    public Result<Page<InspectionRecord>> page(
        @Parameter(description = "堆肥批次ID（可选）") @RequestParam(required = false) Long compostId,
        @Parameter(description = "抽检结果（1合格，0不合格，只有选择时才传递）") @RequestParam(required = false)Integer result,
        @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int size) {
        return Result.success(inspectionRecordService.pageInspection(compostId,result, page, size));
    }

    @Operation(summary = "查询抽检详情", description = "根据ID查询抽检详情")
    @SaCheckLogin
    @GetMapping("/detail/{id}")
    public Result<InspectionRecord> detail(@Parameter(description = "抽检记录ID") @PathVariable Long id) {
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

    @Operation(summary = "导出抽检记录Excel", description = "管理员/质检员导出某堆肥批次下的抽检记录")
    @SaCheckLogin
    @SaCheckRole({"admin", "inspector"})
    @GetMapping("/export")
    public void export(@Parameter(description = "堆肥批次ID") @RequestParam Long compostId,
                      HttpServletResponse response) throws Exception {
        if (compostId == null || compostId <= 0) {
            throw new com.zhang.huinongplatform.common.BizException("堆肥批次ID不能为空且必须大于0");
        }
        inspectionRecordService.exportInspectionExcel(compostId, response);
    }
} 