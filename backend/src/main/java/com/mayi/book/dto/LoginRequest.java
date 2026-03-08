package com.mayi.book.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String code;
    private String nickName;
    private String avatarUrl;
}
