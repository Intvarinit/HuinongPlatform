package com.zhang.huinongplatform.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecoveryStatisticsDTO {
    private List<String> xAxis; // 日期
    private List<Integer> data; // 回收量
} 