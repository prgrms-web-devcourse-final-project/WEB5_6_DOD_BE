package com.grepp.spring.infra.error.exceptions.schedule;

import com.grepp.spring.infra.error.exceptions.CustomException;
import com.grepp.spring.infra.response.ScheduleErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocationNotFoundException extends CustomException {

    public LocationNotFoundException(ScheduleErrorCode code) {
        super(code);
    }

    public LocationNotFoundException(ScheduleErrorCode code, Exception e) {
        super(code, e);
    }

}
