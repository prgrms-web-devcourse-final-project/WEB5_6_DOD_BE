package com.grepp.spring.infra.error.exceptions;

import com.grepp.spring.infra.response.GroupErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotGroupUserException extends RuntimeException {

    private final GroupErrorCode code;

    public NotGroupUserException(GroupErrorCode code) {
        this.code = code;
    }

    public NotGroupUserException(GroupErrorCode code, Exception e) {
        this.code = code;
        log.error(e.getMessage(), e);
    }

    public GroupErrorCode code() {
        return code;
    }
}
