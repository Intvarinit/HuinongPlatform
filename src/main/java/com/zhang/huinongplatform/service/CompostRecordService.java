package com.zhang.huinongplatform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.huinongplatform.entity.CompostRecord;
import com.zhang.huinongplatform.entity.dto.CompostCreateDTO;
import com.zhang.huinongplatform.entity.dto.CompostUpdateDTO;

public interface CompostRecordService {
    void createCompost(CompostCreateDTO dto);
    void updateCompost(CompostUpdateDTO dto);
    CompostRecord getById(Long id);
    Page<CompostRecord> pageByRecoveryId(Long recoveryId, int page, int size);
    void finishCompost(Long id, String remark);
    void markAbnormal(Long id, String remark);
    void exportCompostExcel(javax.servlet.http.HttpServletResponse response);
    void exportByRecoveryIdExcel(Long recoveryId, javax.servlet.http.HttpServletResponse response) throws Exception;
    /**
     * 统计异常率
     */
    double getCompostExceptionRate(String startDate, String endDate);
    /**
     * 统计合格率
     */
    double getCompostPassRate(String startDate, String endDate);
} 