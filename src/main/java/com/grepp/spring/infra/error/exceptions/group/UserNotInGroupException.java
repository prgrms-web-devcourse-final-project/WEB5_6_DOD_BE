package com.grepp.spring.infra.error.exceptions.group;

import com.grepp.spring.infra.response.GroupErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotInGroupException extends RuntimeException {

    private final GroupErrorCode code;

    public UserNotInGroupException(GroupErrorCode code) {
        this.code = code;
    }

    public UserNotInGroupException(GroupErrorCode code, Exception e) {
        this.code = code;
        log.error(e.getMessage(), e);
    }

    public GroupErrorCode code() {
        return code;
    }

}
