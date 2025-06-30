package com.zhang.huinongplatform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.zhang.huinongplatform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "inspection_record", autoResultMap = true)
public class InspectionRecord extends BaseEntity {
    private Long compostId;
    private Long inspectorId;
    private LocalDateTime inspectionTime;
    private Integer result; // 0:合格 1:不合格
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal phValue;
    private String remark;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images; // 图片URL列表，JSON数组
} 