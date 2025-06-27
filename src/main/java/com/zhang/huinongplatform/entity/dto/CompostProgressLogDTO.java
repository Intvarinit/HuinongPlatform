package com.zhang.huinongplatform.entity.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CompostProgressLogDTO {
    private Long compostId;
    private LocalDateTime time;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal phValue;
    private String remark;
} 