package com.zhang.huinongplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhang.huinongplatform.entity.CompostProgressLog;
import com.zhang.huinongplatform.entity.dto.CompostProgressLogDTO;
import com.zhang.huinongplatform.mapper.CompostProgressLogMapper;
import com.zhang.huinongplatform.service.CompostProgressLogService;
import com.zhang.huinongplatform.service.MessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompostProgressLogServiceImpl implements CompostProgressLogService {
    private final CompostProgressLogMapper compostProgressLogMapper;



    private final MessageService messageService;

    @Override
    public void addProgressLog(CompostProgressLogDTO dto) {
        CompostProgressLog log = new CompostProgressLog();
        BeanUtils.copyProperties(dto, log);
        compostProgressLogMapper.insert(log);
        // 只发送Map，所有时间字段转字符串
        java.util.Map<String, Object> msg = new java.util.HashMap<>();
        msg.put("id", log.getId());
        msg.put("compostId", log.getCompostId());
        msg.put("temperature", log.getTemperature());
        msg.put("humidity", log.getHumidity());
        msg.put("phValue", log.getPhValue());
        msg.put("remark", log.getRemark());
        msg.put("time", log.getTime() != null ? log.getTime().toString() : null);
        messageService.sendInspectionNotification(msg);
    }

    @Override
    public List<CompostProgressLog> listByCompostId(Long compostId) {
        return compostProgressLogMapper.selectList(new LambdaQueryWrapper<CompostProgressLog>()
                .eq(CompostProgressLog::getCompostId, compostId)
                .orderByAsc(CompostProgressLog::getTime));
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<CompostProgressLog> pageByCompostId(Long compostId, int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CompostProgressLog> p = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        return compostProgressLogMapper.selectPage(p, new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CompostProgressLog>()
                .eq(CompostProgressLog::getCompostId, compostId)
                .orderByAsc(CompostProgressLog::getTime));
    }
} 