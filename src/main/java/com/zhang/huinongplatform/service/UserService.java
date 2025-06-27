package com.zhang.huinongplatform.service;

import com.zhang.huinongplatform.entity.User;
import com.zhang.huinongplatform.entity.dto.LoginDTO;
import com.zhang.huinongplatform.entity.dto.RegisterDTO;

public interface UserService {
    
    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return token
     */
    String login(LoginDTO loginDTO);
    
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
} 