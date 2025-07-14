package com.grepp.spring.infra.error.exceptions.group;

import com.grepp.spring.infra.response.GroupErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAlreadyInGroupException extends RuntimeException {

    private final GroupErrorCode code;

    public UserAlreadyInGroupException(GroupErrorCode code) {
        this.code = code;
    }

    public UserAlreadyInGroupException(GroupErrorCode code, Exception e) {
        this.code = code;
        log.error(e.getMessage(), e);
    }

    public GroupErrorCode code() {
        return code;
    }

}
