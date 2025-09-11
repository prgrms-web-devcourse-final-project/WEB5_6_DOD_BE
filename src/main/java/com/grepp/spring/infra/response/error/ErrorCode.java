package com.grepp.spring.infra.response.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String code();
    HttpStatus status();
    String message();
}
