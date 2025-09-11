package com.grepp.spring.infra.error.exceptions.event;

import com.grepp.spring.infra.error.exceptions.CustomException;
import com.grepp.spring.infra.response.EventErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidEventDataException extends CustomException {

    public InvalidEventDataException(EventErrorCode code) {
        super(code);
    }

    public InvalidEventDataException(EventErrorCode code, Exception e) {
        super(code, e);
    }

}