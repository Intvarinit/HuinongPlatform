package com.zhang.huinongplatform.entity.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompostCreateDTO {
    private Long recoveryId;
    private String batchNo;
    private LocalDateTime startTime;
    private String remark;
} 