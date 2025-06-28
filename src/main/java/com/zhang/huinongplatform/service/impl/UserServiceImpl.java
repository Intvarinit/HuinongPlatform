package com.zhang.huinongplatform.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhang.huinongplatform.common.BizException;
import com.zhang.huinongplatform.entity.User;
import com.zhang.huinongplatform.entity.dto.LoginDTO;
import com.zhang.huinongplatform.entity.dto.LoginByCodeDTO;
import com.zhang.huinongplatform.entity.dto.RegisterDTO;
import com.zhang.huinongplatform.mapper.UserMapper;
import com.zhang.huinongplatform.service.UserService;
import com.zhang.huinongplatform.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final VerificationCodeService verificationCodeService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String login(LoginDTO loginDTO) {
        // 查询用户
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginDTO.getUsername())
        );
        
        if (user == null) {
            throw new BizException("用户不存在");
        }
        
        // 校验密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BizException("密码错误");
        }
        
        // 校验状态
        if (user.getStatus() == 0) {
            throw new BizException("账号已被禁用");
        }
        
        // 登录
        StpUtil.login(user.getId());
        
        return StpUtil.getTokenValue();
    }

    @Override
    public String loginByCode(LoginByCodeDTO loginByCodeDTO) {
        // 验证验证码
        boolean isValidCode = verificationCodeService.verifyLoginCode(
            loginByCodeDTO.getPhone(), 
            loginByCodeDTO.getCode()
        );
        
        if (!isValidCode) {
            throw new BizException("验证码错误或已过期");
        }
        
        // 根据手机号查询用户
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getPhone, loginByCodeDTO.getPhone())
        );
        
        if (user == null) {
            throw new BizException("用户不存在，请先注册");
        }
        
        // 校验用户状态
        if (user.getStatus() == 0) {
            throw new BizException("账号已被禁用");
        }
        
        // 登录
        StpUtil.login(user.getId());
        
        return StpUtil.getTokenValue();
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        // 检查用户名是否存在
        if (userMapper.selectCount(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, registerDTO.getUsername())
        ) > 0) {
            throw new BizException("用户名已存在");
        }
        
        // 检查手机号是否存在
        if (userMapper.selectCount(
            new LambdaQueryWrapper<User>()
                .eq(User::getPhone, registerDTO.getPhone())
        ) > 0) {
            throw new BizException("手机号已存在");
        }
        
        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setUserType(1); // 默认为农户
        user.setStatus(1); // 默认启用
        
        userMapper.insert(user);
    }

    @Override
    public User getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        return userMapper.selectById(userId);
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }
} 