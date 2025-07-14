package com.grepp.spring.infra.error.exceptions.group;

import com.grepp.spring.infra.response.GroupErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupNotFoundException extends RuntimeException {

    private final GroupErrorCode code;

    public GroupNotFoundException(GroupErrorCode code) {
        this.code = code;
    }

    public GroupNotFoundException(GroupErrorCode code, Exception e) {
        this.code = code;
        log.error(e.getMessage(), e);
    }

    public GroupErrorCode code() {
        return code;
    }

}
