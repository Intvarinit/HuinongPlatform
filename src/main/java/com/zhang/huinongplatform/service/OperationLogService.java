package com.zhang.huinongplatform.service;

import com.zhang.huinongplatform.entity.OperationLog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface OperationLogService {
    void saveLog(OperationLog log);
    Page<OperationLog> pageLogs(int page, int size, String username, String operation);
    /**
     * 根据ID删除操作日志
     */
    void removeById(Long id);
} 