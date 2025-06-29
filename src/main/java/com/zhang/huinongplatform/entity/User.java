package com.zhang.huinongplatform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhang.huinongplatform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {
    
    private String username;
    
    private String password;
    
    private String phone;
    
    private String realName;
    
    private Integer userType; // 1: admin, 2: inspector, 3:user
    
    private String address;
    
    private Integer status; // 0: 禁用, 1: 启用
} 