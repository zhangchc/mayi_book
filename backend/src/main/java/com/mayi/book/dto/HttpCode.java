package com.mayi.book.dto;

/**
 * HTTP 和业务状态码常量，避免在代码中直接使用魔法数字。
 */
public final class HttpCode {

    private HttpCode() {
        // utility class
    }

    public static final int SUCCESS = 200;
    public static final int UNAUTHORIZED = 401;
    public static final int SERVER_ERROR = 500;
}

