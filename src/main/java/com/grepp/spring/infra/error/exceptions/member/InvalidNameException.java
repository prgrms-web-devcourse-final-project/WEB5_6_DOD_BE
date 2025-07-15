package com.grepp.spring.infra.error.exceptions.member;

public class InvalidNameException extends RuntimeException{

    public InvalidNameException(String message) {
        super(message);
    }
}
