package com.zhang.huinongplatform.config;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import com.zhang.huinongplatform.mapper.UserRoleMapper;

@Component
public class StpInterfaceImpl implements StpInterface {
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 如有权限系统可在此返回权限码列表
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null) return java.util.Collections.emptyList();
        return userRoleMapper.getRoleCodesByUserId(Long.valueOf(loginId.toString()));
    }
} 