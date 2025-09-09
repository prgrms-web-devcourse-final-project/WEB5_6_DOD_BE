package com.grepp.spring.infra.error.exceptions.group;

import com.grepp.spring.infra.error.exceptions.CustomException;
import com.grepp.spring.infra.response.GroupErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserGroupLeaderException extends CustomException {

    public UserGroupLeaderException(GroupErrorCode code) {
        super(code);
    }

    public UserGroupLeaderException(GroupErrorCode code, Exception e) {
        super(code, e);
    }
}

