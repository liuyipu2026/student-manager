package com.example.student_web.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获所有 Exception 类型的异常
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        // 打印详细错误日志（方便排查）
        e.printStackTrace();
        return Result.error(500, "服务器内部错误，请联系管理员");
    }

}