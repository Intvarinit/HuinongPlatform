package com.zhang.huinongplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhang.huinongplatform.entity.RecoveryRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecoveryRecordMapper extends BaseMapper<RecoveryRecord> {
    // 按日期统计回收量
    List<Integer> getRecoveryCountByDate(String startDate, String endDate, String groupBy);

    List<String> getDateRange(String startDate, String endDate, String groupBy);
} 