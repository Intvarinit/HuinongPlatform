package com.zhang.huinongplatform.service;

import com.zhang.huinongplatform.entity.RecoveryRecord;
import com.zhang.huinongplatform.entity.dto.RecoveryApplyDTO;
import com.zhang.huinongplatform.entity.dto.RecoveryAuditDTO;

import java.util.List;

public interface RecoveryRecordService {
    /**
     * 农户提交回收申请
     */
    void applyRecovery(RecoveryApplyDTO dto);

    /**
     * 管理员审核回收申请
     */
    void auditRecovery(RecoveryAuditDTO dto);


    /**
     * 查询回收记录详情
     */
    RecoveryRecord getById(Long id);

    /**
     * 查询当前用户的回收记录（分页，默认page=1，size=10）
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecoveryRecord> pageMyRecords(int page, int size, Integer status, String cropType);

    /**
     * 管理员查询所有回收记录（分页，默认page=1，size=10）
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecoveryRecord> pageAllRecords(int page, int size, Integer status, String cropType);

    void deleteRecovery(Long id);

    /**
     * 按日期统计回收量
     */
    List<Integer> getRecoveryCountByDate(String startDate, String endDate, String groupBy);
    List<String> getDateRange(String startDate, String endDate, String groupBy);
} 