package com.grepp.spring.infra.error.exceptions.event;

import com.grepp.spring.infra.response.EventErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlreadyConfirmedScheduleException extends RuntimeException {

    private final EventErrorCode code;

    public AlreadyConfirmedScheduleException(EventErrorCode code) {
        this.code = code;
    }

    public AlreadyConfirmedScheduleException(EventErrorCode code, Exception e) {
        this.code = code;
        log.error(e.getMessage(), e);
    }

    public EventErrorCode code() {
        return code;
    }
}