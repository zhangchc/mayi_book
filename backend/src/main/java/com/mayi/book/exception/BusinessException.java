package com.mayi.book.exception;

import com.mayi.book.dto.HttpCode;

/**
 * 统一业务异常，携带业务状态码，便于全局异常处理。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(HttpCode.SERVER_ERROR, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

