package com.zhang.huinongplatform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhang.huinongplatform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("compost_record")
public class CompostRecord extends BaseEntity {
    private Long recoveryId;
    private String batchNo;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status; // 0:进行中 1:已完成 2:异常
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal phValue;
    private String remark;
} 