package com.zhang.huinongplatform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhang.huinongplatform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("recovery_record")
public class RecoveryRecord extends BaseEntity {
    
    private Long userId;
    
    private String cropType; // 作物类型
    
    private BigDecimal weight; // 重量(kg)
    
    private String location; // 回收地点
    
    private Integer status; // 0: 待审核, 1: 已通过, 2: 已拒绝, 3: 已完成
    
    private String remark; // 备注
    
    private Long operatorId; // 操作人ID
    
    private LocalDateTime appointmentTime; // 预约时间
} 