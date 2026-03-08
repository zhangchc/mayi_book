package com.mayi.book.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mayi.book.dto.LoginRequest;
import com.mayi.book.dto.LoginResponse;
import com.mayi.book.entity.User;
import com.mayi.book.mapper.UserMapper;
import com.mayi.book.utils.JwtUtil;
import com.mayi.book.utils.WechatUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private WechatUtil wechatUtil;

    @Transactional
    public LoginResponse login(LoginRequest request) throws Exception {
        log.info("开始处理登录 - 接收到的昵称: {}, 头像: {}", request.getNickName(), request.getAvatarUrl());
        
        // 通过code获取openid
        JSONObject wechatResponse = wechatUtil.getOpenidByCode(request.getCode());
        if (wechatResponse == null || wechatResponse.containsKey("errcode")) {
            throw new RuntimeException("微信登录失败：" + (wechatResponse != null ? wechatResponse.getString("errmsg") : "未知错误"));
        }
        
        String openid = wechatResponse.getString("openid");
        if (openid == null || openid.isEmpty()) {
            throw new RuntimeException("获取openid失败");
        }
        
        log.info("成功获取openid: {}", openid);
        
        // 查询用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid, openid);
        User user = userMapper.selectOne(queryWrapper);
        
        // 如果用户不存在，创建新用户
        if (user == null) {
            log.info("用户不存在，创建新用户");
            user = new User();
            user.setOpenid(openid);
            user.setNickName(request.getNickName());
            // 只在头像不为空时才设置
            if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
                user.setAvatarUrl(request.getAvatarUrl());
            }
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.insert(user);
            log.info("新用户创建成功 - ID: {}, 昵称: {}, 头像: {}", 
                user.getId(), user.getNickName(), user.getAvatarUrl());
        } else {
            // 更新用户信息
            log.info("用户已存在，更新用户信息 - 原昵称: {}, 新昵称: {}, 原头像: {}, 新头像: {}", 
                user.getNickName(), request.getNickName(), user.getAvatarUrl(), request.getAvatarUrl());
            
            // 更新昵称（如果提供）
            if (request.getNickName() != null && !request.getNickName().isEmpty()) {
                user.setNickName(request.getNickName());
            }
            
            // 只在头像不为空时才更新（避免用空值覆盖已有的头像）
            if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
                user.setAvatarUrl(request.getAvatarUrl());
                log.info("更新用户头像: {}", request.getAvatarUrl());
            } else {
                log.warn("警告：未获取到用户头像，保持原有头像不变");
            }
            
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
            log.info("用户信息更新成功 - 昵称: {}, 头像: {}", user.getNickName(), user.getAvatarUrl());
        }
        
        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getOpenid());
        
        // 返回登录响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setOpenid(user.getOpenid());
        
        log.info("登录成功 - userId: {}, openid: {}, nickName: {}", user.getId(), user.getOpenid(), user.getNickName());
        
        return response;
    }

    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    public User getUserByOpenid(String openid) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid, openid);
        return userMapper.selectOne(queryWrapper);
    }
}
