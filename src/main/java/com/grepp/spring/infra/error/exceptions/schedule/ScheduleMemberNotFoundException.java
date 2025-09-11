package com.grepp.spring.infra.error.exceptions.schedule;

import com.grepp.spring.infra.error.exceptions.CustomException;
import com.grepp.spring.infra.response.ScheduleErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScheduleMemberNotFoundException extends CustomException {

    public ScheduleMemberNotFoundException(ScheduleErrorCode code) {
        super(code);
    }

    public ScheduleMemberNotFoundException(ScheduleErrorCode code, Exception e) {
        super(code, e);
    }

}
