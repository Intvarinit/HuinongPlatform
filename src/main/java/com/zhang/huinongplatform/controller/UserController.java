package com.zhang.huinongplatform.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.zhang.huinongplatform.common.Result;
import com.zhang.huinongplatform.entity.User;
import com.zhang.huinongplatform.entity.dto.LoginDTO;
import com.zhang.huinongplatform.entity.dto.RegisterDTO;
import com.zhang.huinongplatform.service.UserService;
import com.zhang.huinongplatform.annotation.OperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "用户管理", description = "用户注册、登录、信息、注销等接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户登录", description = "用户名密码登录，返回token")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return Result.success(token);
    }

    @Operation(summary = "用户注册", description = "注册新用户")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @SaCheckLogin
    @GetMapping("/info")
    public Result<User> getCurrentUser() {
        User user = userService.getCurrentUser();
        return Result.success(user);
    }

    @Operation(summary = "用户注销", description = "退出登录，清除token")
    @SaCheckLogin
    @PostMapping("/logout")
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }
} 