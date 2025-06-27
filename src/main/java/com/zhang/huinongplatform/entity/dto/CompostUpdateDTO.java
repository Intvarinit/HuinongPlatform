package com.zhang.huinongplatform.entity.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CompostUpdateDTO {
    private Long id;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal phValue;
    private String remark;
    private LocalDateTime endTime;
    private Integer status; // 可选，0:进行中 1:已完成 2:异常
} 