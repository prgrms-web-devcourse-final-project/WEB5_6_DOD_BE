package com.grepp.spring.infra.error.exceptions.event;

import com.grepp.spring.infra.response.EventErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlreadyCompletedScheduleException extends RuntimeException {

    private final EventErrorCode code;

    public AlreadyCompletedScheduleException(EventErrorCode code) {
        this.code = code;
    }

    public AlreadyCompletedScheduleException(EventErrorCode code, Exception e) {
        this.code = code;
        log.error(e.getMessage(), e);
    }

    public EventErrorCode code() {
        return code;
    }
}