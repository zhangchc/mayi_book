package com.mayi.book.config;

import com.mayi.book.dto.ApiResponse;
import com.mayi.book.dto.HttpCode;
import com.mayi.book.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理，兜底返回统一响应结构。
 * 目前仅作为保护网，不改变现有 Controller 的 try-catch 行为。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        log.warn("业务异常: code={}, message={}", ex.getCode(), ex.getMessage());
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex) {
        log.error("未捕获异常", ex);
        // 保持与现有前端交互兼容：统一返回 500 + 通用提示
        return ApiResponse.error(HttpCode.SERVER_ERROR, "系统异常，请稍后重试");
    }
}

