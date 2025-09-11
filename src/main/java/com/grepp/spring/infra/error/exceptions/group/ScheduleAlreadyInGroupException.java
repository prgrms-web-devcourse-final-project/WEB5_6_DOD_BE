package com.grepp.spring.infra.error.exceptions.group;

import com.grepp.spring.infra.error.exceptions.CustomException;
import com.grepp.spring.infra.response.GroupErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScheduleAlreadyInGroupException extends CustomException {

    public ScheduleAlreadyInGroupException(GroupErrorCode code) {
        super(code);
    }

    public ScheduleAlreadyInGroupException(GroupErrorCode code, Exception e) {
        super(code, e);
    }
}
