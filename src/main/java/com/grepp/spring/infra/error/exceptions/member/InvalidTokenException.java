package com.grepp.spring.infra.error.exceptions.member;

import com.grepp.spring.infra.response.ResponseCode;

public class InvalidTokenException extends RuntimeException {
    private final ResponseCode responseCode;

    public InvalidTokenException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}