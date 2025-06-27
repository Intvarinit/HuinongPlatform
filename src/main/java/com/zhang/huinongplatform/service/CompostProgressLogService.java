package com.zhang.huinongplatform.service;

import com.zhang.huinongplatform.entity.CompostProgressLog;
import com.zhang.huinongplatform.entity.dto.CompostProgressLogDTO;
import java.util.List;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface CompostProgressLogService {
    void addProgressLog(CompostProgressLogDTO dto);
    List<CompostProgressLog> listByCompostId(Long compostId);
    Page<CompostProgressLog> pageByCompostId(Long compostId, int page, int size);
} 