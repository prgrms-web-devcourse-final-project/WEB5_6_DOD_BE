package com.grepp.spring.infra.error.exceptions.group;

import com.grepp.spring.infra.response.GroupErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupAuthenticationException extends RuntimeException {

    private final GroupErrorCode code;

    public GroupAuthenticationException(GroupErrorCode code) {
        this.code = code;
    }

    public GroupAuthenticationException(GroupErrorCode code, Exception e) {
        this.code = code;
        log.error(e.getMessage(), e);
    }

    public GroupErrorCode code() {
        return code;
    }

}
