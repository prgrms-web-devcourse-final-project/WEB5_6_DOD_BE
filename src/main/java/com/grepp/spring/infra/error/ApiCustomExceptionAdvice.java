package com.grepp.spring.infra.error;

import com.grepp.spring.infra.error.exceptions.CustomException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.grepp.spring.app.controller.api")
@Order(0)
public class ApiCustomExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("API CustomException occurred: code={}, message={}", errorCode.code(), errorCode.message());

        return ResponseEntity
            .status(errorCode.status())
            .body(ApiResponse.error(errorCode));
    }
}