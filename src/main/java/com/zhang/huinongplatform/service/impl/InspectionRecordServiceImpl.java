package com.zhang.huinongplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.huinongplatform.common.BizException;
import com.zhang.huinongplatform.entity.InspectionRecord;
import com.zhang.huinongplatform.entity.dto.InspectionCreateDTO;
import com.zhang.huinongplatform.mapper.InspectionRecordMapper;
import com.zhang.huinongplatform.service.InspectionRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.excel.EasyExcel;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InspectionRecordServiceImpl implements InspectionRecordService {
    private final InspectionRecordMapper inspectionRecordMapper;
    private final com.zhang.huinongplatform.mapper.CompostRecordMapper compostRecordMapper;
    private final com.zhang.huinongplatform.mapper.RecoveryRecordMapper recoveryRecordMapper;
    private final com.zhang.huinongplatform.service.MessageService messageService;

    @Override
    public void createInspection(InspectionCreateDTO dto) {
        InspectionRecord record = new InspectionRecord();
        BeanUtils.copyProperties(dto, record);
        record.setInspectorId(StpUtil.getLoginIdAsLong());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        inspectionRecordMapper.insert(record);
        // 异常自动通知
        if (dto.getResult() != null && dto.getResult() == 1) {
            // 发送结构化异常消息，便于消费者处理
            Map<String, Object> msg = new java.util.HashMap<>();
            msg.put("compostId", dto.getCompostId());
            msg.put("inspectorId", record.getInspectorId());
            msg.put("inspectionTime", record.getInspectionTime() != null ? record.getInspectionTime().toString() : null);
            msg.put("remark", dto.getRemark());
            msg.put("result", dto.getResult());
            msg.put("type", "inspection_abnormal");
            messageService.sendInspectionNotification(msg);
        }
    }

    @Override
    public Page<InspectionRecord> pageInspection(Long compostId, Integer result, int page, int size) {
        Page<InspectionRecord> p = new Page<>(page, size);
        LambdaQueryWrapper<InspectionRecord> wrapper = new LambdaQueryWrapper<InspectionRecord>()
                .eq(InspectionRecord::getDeleted, 0)
                .orderByDesc(InspectionRecord::getInspectionTime);
        
        if (compostId != null && compostId > 0) {
            // 查询指定堆肥批次下的抽检记录
            wrapper.eq(InspectionRecord::getCompostId, compostId);
            // 权限控制：普通用户只能查自己相关的compostId
            if (!StpUtil.hasRole("admin") && !StpUtil.hasRole("inspector")) {
                if (!isCompostBelongToUser(compostId, StpUtil.getLoginIdAsLong())) {
                    throw new BizException("无权查看该抽检记录");
                }
            }
        } else {
            // 查询所有抽检记录，需要管理员权限
            if (!StpUtil.hasRole("admin") && !StpUtil.hasRole("inspector")) {
                throw new BizException("需要管理员权限才能查看所有抽检记录");
            }
        }
        // 修复：result=0 也能筛选
        if (result != null) {
            wrapper.eq(InspectionRecord::getResult, result);
        }
        return inspectionRecordMapper.selectPage(p, wrapper);
    }

    @Override
    public InspectionRecord getById(Long id) {
        InspectionRecord record = inspectionRecordMapper.selectById(id);
        if (record == null || record.getDeleted() == 1) {
            throw new BizException("抽检记录不存在");
        }
        return record;
    }

    @Override
    public void exportInspectionExcel(Long compostId, HttpServletResponse response) throws Exception {
        LambdaQueryWrapper<InspectionRecord> wrapper = new LambdaQueryWrapper<InspectionRecord>()
                .eq(InspectionRecord::getCompostId, compostId)
                .eq(InspectionRecord::getDeleted, 0)
                .orderByDesc(InspectionRecord::getInspectionTime);
        List<InspectionRecord> list = inspectionRecordMapper.selectList(wrapper);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = java.net.URLEncoder.encode("inspection_records.xlsx", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        EasyExcel.write(response.getOutputStream(), InspectionRecord.class).sheet("抽检记录").doWrite(list);
    }

    @Override
    public boolean isCompostBelongToUser(Long compostId, Long userId) {
        com.zhang.huinongplatform.entity.CompostRecord compost = compostRecordMapper.selectById(compostId);
        if (compost == null) return false;
        com.zhang.huinongplatform.entity.RecoveryRecord recovery = recoveryRecordMapper.selectById(compost.getRecoveryId());
        return recovery != null && recovery.getUserId().equals(userId);
    }
} 