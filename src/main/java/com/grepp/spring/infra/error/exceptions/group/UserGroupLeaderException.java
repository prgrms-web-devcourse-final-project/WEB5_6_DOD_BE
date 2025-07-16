package com.grepp.spring.infra.error.exceptions.group;

import com.grepp.spring.infra.response.GroupErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserGroupLeaderException extends RuntimeException {

    private final GroupErrorCode code;

    public UserGroupLeaderException(GroupErrorCode code) {
        this.code = code;
    }

    public UserGroupLeaderException(GroupErrorCode code, Exception e) {
        this.code = code;
        log.error(e.getMessage(), e);
    }

    public GroupErrorCode code() {
        return code;
    }

}

