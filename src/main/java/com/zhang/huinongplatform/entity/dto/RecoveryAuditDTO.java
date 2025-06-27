package com.zhang.huinongplatform.entity.dto;

import lombok.Data;

@Data
public class RecoveryAuditDTO {
    private Long id; // 回收记录ID
    private Integer status; // 审核状态 1:通过 2:拒绝
    private String remark; // 审核备注
} 