package com.grepp.spring.infra.error.exceptions;

import com.grepp.spring.infra.response.error.ErrorCode;
import lombok.Getter;

@Getter
public abstract class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, Exception e) {
        super(errorCode.message(), e);
        this.errorCode = errorCode;
    }
}