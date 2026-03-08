package com.mayi.book.controller;

import com.mayi.book.dto.ApiResponse;
import com.mayi.book.dto.LoginRequest;
import com.mayi.book.dto.LoginResponse;
import com.mayi.book.entity.User;
import com.mayi.book.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            log.info("收到登录请求 - code: {}, nickName: {}, avatarUrl: {}", 
                request.getCode(), 
                request.getNickName(), 
                request.getAvatarUrl());
            
            if (request.getNickName() == null || request.getNickName().isEmpty()) {
                log.warn("警告：用户昵称为空！前端可能未成功获取用户信息");
            }
            
            if (request.getAvatarUrl() == null || request.getAvatarUrl().isEmpty()) {
                log.warn("警告：用户头像为空！前端可能未成功获取用户头像（微信API限制）");
            }
            
            LoginResponse response = userService.login(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("登录失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public ApiResponse<User> getUserInfo(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ApiResponse.error(401, "未授权");
            }
            User user = userService.getUserById(userId);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
