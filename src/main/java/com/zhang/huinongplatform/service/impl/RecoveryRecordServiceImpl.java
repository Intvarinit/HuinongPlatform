package com.zhang.huinongplatform.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zhang.huinongplatform.common.BizException;
import com.zhang.huinongplatform.entity.RecoveryRecord;
import com.zhang.huinongplatform.entity.dto.RecoveryApplyDTO;
import com.zhang.huinongplatform.entity.dto.RecoveryAuditDTO;
import com.zhang.huinongplatform.mapper.RecoveryRecordMapper;
import com.zhang.huinongplatform.service.RecoveryRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecoveryRecordServiceImpl implements RecoveryRecordService {
    private final RecoveryRecordMapper recoveryRecordMapper;

    @Override
    public void applyRecovery(RecoveryApplyDTO dto) {
        RecoveryRecord record = new RecoveryRecord();
        BeanUtils.copyProperties(dto, record);
        record.setUserId(StpUtil.getLoginIdAsLong());
        record.setStatus(0); // 待审核
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        recoveryRecordMapper.insert(record);
    }

    @Override
    public void auditRecovery(RecoveryAuditDTO dto) {
        RecoveryRecord record = recoveryRecordMapper.selectById(dto.getId());
        if (record == null) {
            throw new BizException("回收记录不存在");
        }
        record.setStatus(dto.getStatus());
        record.setRemark(dto.getRemark());
        record.setUpdateTime(LocalDateTime.now());
        recoveryRecordMapper.updateById(record);
    }

    @Override
    public RecoveryRecord getById(Long id) {
        RecoveryRecord record = recoveryRecordMapper.selectById(id);
        if (record == null) {
            throw new BizException("回收记录不存在");
        }

        // 判断权限：管理员可查任意，普通用户只能查自己的
        if (!StpUtil.hasRole("admin") && !record.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new BizException("无权查看该回收记录");
        }
        return record;
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecoveryRecord> pageMyRecords(int page, int size) {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecoveryRecord> p = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        return recoveryRecordMapper.selectPage(p, new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RecoveryRecord>()
                .eq(RecoveryRecord::getUserId, userId)
                .eq(RecoveryRecord::getDeleted, 0)
                .orderByDesc(RecoveryRecord::getCreateTime));
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecoveryRecord> pageAllRecords(int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecoveryRecord> p = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        return recoveryRecordMapper.selectPage(p, new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RecoveryRecord>()
                .eq(RecoveryRecord::getDeleted, 0)
                .orderByDesc(RecoveryRecord::getCreateTime));
    }

    @Override
    public void deleteRecovery(Long id) {
        RecoveryRecord record = recoveryRecordMapper.selectById(id);
        if (record == null) throw new BizException("记录不存在");
        if (!StpUtil.hasRole("admin") && !record.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new BizException("无权删除该记录");
        }
        // 使用UpdateWrapper强制更新deleted字段
        UpdateWrapper<RecoveryRecord> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id);
        wrapper.set("deleted", 1);
        recoveryRecordMapper.update(null, wrapper);
    }

    @Override
    public List<Integer> getRecoveryCountByDate(String startDate, String endDate, String groupBy) {
        return recoveryRecordMapper.getRecoveryCountByDate(startDate, endDate, groupBy);
    }

    @Override
    public List<String> getDateRange(String startDate, String endDate, String groupBy) {
        return recoveryRecordMapper.getDateRange(startDate, endDate, groupBy);
    }
} 