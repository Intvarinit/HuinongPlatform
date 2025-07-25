package com.zhang.huinongplatform.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.zhang.huinongplatform.common.BizException;
import com.zhang.huinongplatform.entity.CompostRecord;
import com.zhang.huinongplatform.entity.RecoveryRecord;
import com.zhang.huinongplatform.entity.User;
import com.zhang.huinongplatform.entity.dto.CompostCreateDTO;
import com.zhang.huinongplatform.entity.dto.CompostUpdateDTO;
import com.zhang.huinongplatform.mapper.CompostRecordMapper;
import com.zhang.huinongplatform.mapper.RecoveryRecordMapper;
import com.zhang.huinongplatform.mapper.UserMapper;
import com.zhang.huinongplatform.service.CompostRecordService;
import com.zhang.huinongplatform.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompostRecordServiceImpl implements CompostRecordService {
    private final CompostRecordMapper compostRecordMapper;
    private final RecoveryRecordMapper recoveryRecordMapper;
    private final UserMapper userMapper;
    private final MessageService messageService;

    @Override
    public void createCompost(CompostCreateDTO dto) {
        CompostRecord record = new CompostRecord();
        BeanUtils.copyProperties(dto, record);
        // 批次号自动生成
        if (dto.getBatchNo() == null || dto.getBatchNo().trim().isEmpty()) {
            String batchNo = "C" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                    + dto.getRecoveryId();
            record.setBatchNo(batchNo);
        }
        record.setStatus(0); // 进行中
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        compostRecordMapper.insert(record);
    }

    @Override
    public void updateCompost(CompostUpdateDTO dto) {
        CompostRecord record = compostRecordMapper.selectById(dto.getId());
        if (record == null) {
            throw new BizException("堆肥批次不存在");
        }
        BeanUtils.copyProperties(dto, record);
        record.setUpdateTime(LocalDateTime.now());
        compostRecordMapper.updateById(record);
    }

    @Override
    public CompostRecord getById(Long id) {
        CompostRecord record = compostRecordMapper.selectById(id);
        if (record == null) {
            throw new BizException("堆肥批次不存在");
        }
        System.out.println("-------------------->" + StpUtil.getRoleList() + "  " + StpUtil.getTokenValue());
        // 权限细化：普通用户只能查自己相关的堆肥批次
        if (!StpUtil.hasRole("admin")) {
            RecoveryRecord rec = recoveryRecordMapper.selectById(record.getRecoveryId());
            if (rec == null || !rec.getUserId().equals(StpUtil.getLoginIdAsLong())) {
                throw new BizException("无权查看该堆肥批次");
            }
        }
        return record;
    }

    @Override
    public void finishCompost(Long id, String remark) {
        CompostRecord record = compostRecordMapper.selectById(id);
        if (record == null) {
            throw new BizException("堆肥批次不存在");
        }
        record.setStatus(1); // 已完成
        record.setEndTime(LocalDateTime.now());
        record.setRemark(remark);
        record.setUpdateTime(LocalDateTime.now());
        compostRecordMapper.updateById(record);
        
        // 发送完成通知
        java.util.Map<String, Object> msg = new java.util.HashMap<>();
        msg.put("msgId", java.util.UUID.randomUUID().toString());
        msg.put("batchNo", record.getBatchNo());
        msg.put("status", "已完成");
        msg.put("endTime", record.getEndTime() != null ? record.getEndTime().toString() : null);
        messageService.sendDataSync(msg);
        
        // 发送短信通知
        try {
            // 根据recoveryId查询用户手机号
            RecoveryRecord recovery = recoveryRecordMapper.selectById(record.getRecoveryId());
            if (recovery != null) {
                User user = userMapper.selectById(recovery.getUserId());
                if (user != null && user.getPhone() != null) {
                    String templateParam = "{\"batchNo\":\"" + record.getBatchNo() + "\",\"status\":\"已完成\",\"endTime\":\"" + 
                            (record.getEndTime() != null ? record.getEndTime().toString() : "") + "\"}";
                    messageService.sendBusinessSmsNotification(user.getPhone(), templateParam);
                    log.info("堆肥完成短信通知发送成功 - 手机号: {}, 批次号: {}", user.getPhone(), record.getBatchNo());
                } else {
                    log.warn("未找到用户信息或用户手机号为空 - userId: {}", recovery.getUserId());
                }
            }
        } catch (Exception e) {
            // 短信发送失败不影响主流程
            log.error("发送完成通知短信失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void markAbnormal(Long id, String remark) {
        CompostRecord record = compostRecordMapper.selectById(id);
        if (record == null) {
            throw new BizException("堆肥批次不存在");
        }
        record.setStatus(2); // 异常
        record.setRemark(remark);
        record.setUpdateTime(LocalDateTime.now());
        compostRecordMapper.updateById(record);
        
        // 发送异常通知
        java.util.Map<String, Object> msg = new java.util.HashMap<>();
        msg.put("msgId", java.util.UUID.randomUUID().toString());
        msg.put("batchNo", record.getBatchNo());
        msg.put("status", "异常");
        msg.put("updateTime", record.getUpdateTime() != null ? record.getUpdateTime().toString() : null);
        msg.put("remark", record.getRemark());
        messageService.sendDataSync(msg);
        
        // 发送短信通知
        try {
            // 根据recoveryId查询用户手机号
            RecoveryRecord recovery = recoveryRecordMapper.selectById(record.getRecoveryId());
            if (recovery != null) {
                User user = userMapper.selectById(recovery.getUserId());
                if (user != null && user.getPhone() != null) {
                    String templateParam = "{\"batchNo\":\"" + record.getBatchNo() + "\",\"status\":\"异常\",\"remark\":\"" + 
                            (record.getRemark() != null ? record.getRemark() : "") + "\"}";
                    messageService.sendBusinessSmsNotification(user.getPhone(), templateParam);
                    log.info("堆肥异常短信通知发送成功 - 手机号: {}, 批次号: {}, 原因: {}", 
                            user.getPhone(), record.getBatchNo(), record.getRemark());
                } else {
                    log.warn("未找到用户信息或用户手机号为空 - userId: {}", recovery.getUserId());
                }
            }
        } catch (Exception e) {
            // 短信发送失败不影响主流程
            log.error("发送异常通知短信失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<CompostRecord> pageByRecoveryId(Long recoveryId, int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CompostRecord> p = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CompostRecord> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CompostRecord>()
                .eq(CompostRecord::getRecoveryId, recoveryId)
                .orderByDesc(CompostRecord::getCreateTime);
        // 权限细化：普通用户只能查自己相关的recoveryId
        if (!cn.dev33.satoken.stp.StpUtil.hasRole("admin")) {
            RecoveryRecord rec = recoveryRecordMapper.selectById(recoveryId);
            if (rec == null || !rec.getUserId().equals(cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong())) {
                throw new com.zhang.huinongplatform.common.BizException("无权查看该回收记录下的堆肥批次");
            }
        }
        return compostRecordMapper.selectPage(p, wrapper);
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<CompostRecord> pageCompost(Long recoveryId, Integer status, int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CompostRecord> p = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CompostRecord> wrapper;
        
        if (recoveryId != null) {
            // 查询指定回收记录下的堆肥批次
            wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CompostRecord>()
                    .eq(CompostRecord::getRecoveryId, recoveryId)
                    .orderByDesc(CompostRecord::getCreateTime);
            
            // 权限控制：普通用户只能查自己相关的recoveryId
            if (!cn.dev33.satoken.stp.StpUtil.hasRole("admin")) {
                RecoveryRecord rec = recoveryRecordMapper.selectById(recoveryId);
                if (rec == null || !rec.getUserId().equals(cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong())) {
                    throw new com.zhang.huinongplatform.common.BizException("无权查看该回收记录下的堆肥批次");
                }
            }
        } else {
            // 查询所有堆肥批次，需要管理员权限
            if (!cn.dev33.satoken.stp.StpUtil.hasRole("admin")) {
                throw new com.zhang.huinongplatform.common.BizException("需要管理员权限才能查看所有堆肥批次");
            }
            wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CompostRecord>()
                    .orderByDesc(CompostRecord::getCreateTime);
        }
        
        // 添加状态筛选条件
        if (status != null) {
            wrapper.eq(CompostRecord::getStatus, status);
        }
        
        return compostRecordMapper.selectPage(p, wrapper);
    }

    @Override
    public void exportCompostExcel(javax.servlet.http.HttpServletResponse response) {
        java.util.List<CompostRecord> list = compostRecordMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CompostRecord>().eq(CompostRecord::getDeleted, 0));
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=compost.xlsx");
            com.alibaba.excel.EasyExcel.write(response.getOutputStream(), CompostRecord.class).sheet("堆肥批次").doWrite(list);
        } catch (Exception e) {
            throw new com.zhang.huinongplatform.common.BizException("导出失败");
        }
    }

    @Override
    public void exportByRecoveryIdExcel(Long recoveryId, javax.servlet.http.HttpServletResponse response) throws Exception {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CompostRecord> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CompostRecord>()
                .eq(CompostRecord::getRecoveryId, recoveryId)
                .eq(CompostRecord::getDeleted, 0)
                .orderByDesc(CompostRecord::getCreateTime);
        java.util.List<CompostRecord> list = compostRecordMapper.selectList(wrapper);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = java.net.URLEncoder.encode("compost_records.xlsx", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        com.alibaba.excel.EasyExcel.write(response.getOutputStream(), CompostRecord.class).sheet("堆肥批次").doWrite(list);
    }

    @Override
    public double getCompostExceptionRate(String startDate, String endDate) {
        int exceptionCount = compostRecordMapper.countException(startDate, endDate);
        int totalCount = compostRecordMapper.countTotal(startDate, endDate);
        if (totalCount == 0) return 0.0;
        return (double) exceptionCount / totalCount;
    }

    @Override
    public double getCompostPassRate(String startDate, String endDate) {
        int passCount = compostRecordMapper.countPass(startDate, endDate);
        int totalCount = compostRecordMapper.countTotal(startDate, endDate);
        if (totalCount == 0) return 0.0;
        return (double) passCount / totalCount;
    }
} 