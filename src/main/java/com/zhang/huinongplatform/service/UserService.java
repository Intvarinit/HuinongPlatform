package com.zhang.huinongplatform.service;

import com.zhang.huinongplatform.entity.User;
import com.zhang.huinongplatform.entity.dto.LoginDTO;
import com.zhang.huinongplatform.entity.dto.LoginByCodeDTO;
import com.zhang.huinongplatform.entity.dto.RegisterDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface UserService {
    
    /**
     * 用户登录（用户名密码）
     * @param loginDTO 登录信息
     * @return token
     */
    String login(LoginDTO loginDTO);
    
    /**
     * 用户登录（验证码）
     * @param loginByCodeDTO 验证码登录信息
     * @return token
     */
    String loginByCode(LoginByCodeDTO loginByCodeDTO);
    
    /**
     * 用户注册
     * @param registerDTO 注册信息
     */
    void register(RegisterDTO registerDTO);
    
    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    User getCurrentUser();
    
    /**
     * 退出登录
     */
    void logout();

    IPage<User> getUserList(int pageNum, int pageSize, Integer userType, Integer status);

    void updateUserType(Long id, Integer userType);

    void updateUserStatus(Long id, Integer status);
} 