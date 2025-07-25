package com.zhang.huinongplatform.entity.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InspectionCreateDTO {
    private Long compostId;
    private LocalDateTime inspectionTime;
    private Integer result; // 0:合格 1:不合格
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal phValue;
    private String remark;
    private List<String> images; // 图片URL列表
} 