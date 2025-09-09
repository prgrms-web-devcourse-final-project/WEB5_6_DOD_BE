package com.grepp.spring.infra.error.exceptions.group;

import com.grepp.spring.infra.error.exceptions.CustomException;
import com.grepp.spring.infra.response.GroupErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAlreadyInGroupException extends CustomException {

    public UserAlreadyInGroupException(GroupErrorCode code) {
        super(code);
    }

    public UserAlreadyInGroupException(GroupErrorCode code, Exception e) {
        super(code, e);
    }

}
