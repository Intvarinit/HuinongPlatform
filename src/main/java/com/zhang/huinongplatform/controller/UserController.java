package com.zhang.huinongplatform.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.zhang.huinongplatform.common.Result;
import com.zhang.huinongplatform.entity.User;
import com.zhang.huinongplatform.entity.dto.LoginDTO;
import com.zhang.huinongplatform.entity.dto.LoginByCodeDTO;
import com.zhang.huinongplatform.entity.dto.RegisterDTO;
import com.zhang.huinongplatform.entity.dto.SendCodeDTO;
import com.zhang.huinongplatform.service.UserService;
import com.zhang.huinongplatform.service.VerificationCodeService;
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
    private final VerificationCodeService verificationCodeService;

    @Operation(summary = "用户登录（用户名密码）", description = "用户名密码登录，返回token")
    @OperationLog("用户登录(账号密码)")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return Result.success(token);
    }

    @Operation(summary = "发送登录验证码", description = "发送登录验证码到指定手机号")
    @PostMapping("/send-login-code")
    public Result<Boolean> sendLoginCode(@RequestBody SendCodeDTO sendCodeDTO) {
        boolean success = verificationCodeService.sendLoginCode(sendCodeDTO.getPhone());
        return Result.success(success);
    }

    @OperationLog("用户登录(验证码)")
    @Operation(summary = "用户登录（验证码）", description = "验证码登录，返回token")
    @PostMapping("/login-by-code")
    public Result<String> loginByCode(@RequestBody LoginByCodeDTO loginByCodeDTO) {
        String token = userService.loginByCode(loginByCodeDTO);
        return Result.success(token);
    }

    @OperationLog("用户注册")
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

    @OperationLog("用户注销")
    @Operation(summary = "用户注销", description = "退出登录，清除token")
    @SaCheckLogin
    @PostMapping("/logout")
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }

//    @Operation(summary = "查看验证码（调试用）", description = "查看Redis中存储的验证码，仅用于开发调试")
//    @GetMapping("/debug/code/{phone}")
//    public Result<String> getStoredCode(@Parameter(description = "手机号") @PathVariable String phone) {
//        String code = verificationCodeService.getStoredCode(phone);
//        return Result.success(code);
//    }
} 