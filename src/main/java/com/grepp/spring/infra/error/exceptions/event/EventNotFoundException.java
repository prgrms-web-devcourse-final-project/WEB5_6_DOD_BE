package com.grepp.spring.infra.error.exceptions.event;

import com.grepp.spring.infra.error.exceptions.CustomException;
import com.grepp.spring.infra.response.EventErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventNotFoundException extends CustomException {

    public EventNotFoundException(EventErrorCode code) {
        super(code);
    }

    public EventNotFoundException(EventErrorCode code, Exception e) {
        super(code, e);
    }

}