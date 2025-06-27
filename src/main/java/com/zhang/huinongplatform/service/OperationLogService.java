package com.zhang.huinongplatform.service;

import com.zhang.huinongplatform.entity.OperationLog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface OperationLogService {
    void saveLog(OperationLog log);
    Page<OperationLog> pageLogs(int page, int size, String username, String operation);
} 