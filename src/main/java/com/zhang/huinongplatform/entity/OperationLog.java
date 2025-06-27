package com.zhang.huinongplatform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("operation_log")
public class OperationLog {
    private Long id;
    private Long userId;
    private String username;
    private String operation; // 操作类型/名称
    private String method;    // 方法名或接口路径
    private String params;    // 请求参数
    private String ip;
    private String result;    // 成功/失败
    private String errorMsg;  // 异常信息
    private Date createTime;
} 