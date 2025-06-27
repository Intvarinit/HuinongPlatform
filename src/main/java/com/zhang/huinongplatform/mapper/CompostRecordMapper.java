package com.zhang.huinongplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhang.huinongplatform.entity.CompostRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompostRecordMapper extends BaseMapper<CompostRecord> {
    // 统计异常批次数
    int countException(String startDate, String endDate);
    // 统计合格批次数
    int countPass(String startDate, String endDate);
    // 统计总批次数
    int countTotal(String startDate, String endDate);
} 