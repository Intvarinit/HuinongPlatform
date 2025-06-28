package com.zhang.huinongplatform.entity.dto;

import lombok.Data;

@Data
public class LoginByCodeDTO {
    private String phone; // 手机号
    private String code;  // 验证码
} 