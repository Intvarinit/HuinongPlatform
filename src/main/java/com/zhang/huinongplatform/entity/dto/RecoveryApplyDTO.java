package com.zhang.huinongplatform.entity.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecoveryApplyDTO {
    private String cropType;
    private BigDecimal weight;
    private String location;
    private String remark;
    private LocalDateTime appointmentTime;
    private List<String> images;
} 