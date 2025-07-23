package com.grepp.spring.infra.error.exceptions.schedule;

import com.grepp.spring.infra.response.ScheduleErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkSpaceNotFoundException extends RuntimeException {
    private final ScheduleErrorCode code;

    public WorkSpaceNotFoundException(ScheduleErrorCode code) {
        this.code = code;
    }

    public WorkSpaceNotFoundException(ScheduleErrorCode code, Exception e) {
        this.code = code;
        log.error(e.getMessage(), e);
    }

    public ScheduleErrorCode code() {
        return code;
    }
}
