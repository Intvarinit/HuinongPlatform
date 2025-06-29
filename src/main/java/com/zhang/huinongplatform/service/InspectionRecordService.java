package com.zhang.huinongplatform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.huinongplatform.entity.InspectionRecord;
import com.zhang.huinongplatform.entity.dto.InspectionCreateDTO;
import javax.servlet.http.HttpServletResponse;

public interface InspectionRecordService {
    void createInspection(InspectionCreateDTO dto);
    Page<InspectionRecord> pageInspection(Long compostId, Integer result, int page, int size);
    InspectionRecord getById(Long id);
    void exportInspectionExcel(Long compostId, HttpServletResponse response) throws Exception;
    boolean isCompostBelongToUser(Long compostId, Long userId);
} 