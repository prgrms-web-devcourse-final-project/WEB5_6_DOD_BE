package com.grepp.spring.infra.response;

import org.springframework.http.HttpStatus;

public enum GroupErrorCode {

    AUTHENTICATION_REQUIRED("401", HttpStatus.UNAUTHORIZED, "로그인이 필요합니다. "),
    NOT_GROUP_LEADER("403", HttpStatus.FORBIDDEN,"해당 그룹의 그룹장이 아닙니다. 권한을 확인해주세요"),
    NOT_GROUP_MEMBER("403", HttpStatus.FORBIDDEN,"해당 그룹의 그룹원이 아닙니다. 권한을 확인해주세요"),
    NOT_SCHEDULE_LEADER("403", HttpStatus.FORBIDDEN,"해당 일정의 팀장이 아닙니다. 권한을 확인해주세요"),
    NOT_SCHEDULE_MEMBER("403", HttpStatus.FORBIDDEN,"해당 일정의 구성원이 아닙니다. 권한을 확인해주세요"),
    GROUP_NOT_FOUND("404", HttpStatus.NOT_FOUND,"존재하지 않는 그룹입니다."),
    USER_NOT_FOUND("404", HttpStatus.NOT_FOUND,"존재하지 않는 유저입니다."),
    SCHEDULE_NOT_FOUND("404", HttpStatus.NOT_FOUND,"존재하지 않는 일정입니다."),
    SCHEDULE_ALREADY_IN_GROUP("409", HttpStatus.CONFLICT,"이미 그룹에 존재한 일정입니다."),
    USER_ALREADY_IN_GROUP("409", HttpStatus.CONFLICT,"이미 그룹에 존재한 유저입니다."),
    ONE_GROUP_LEADER("409", HttpStatus.CONFLICT,"그룹 내 유일한 그룹장입니다. 그룹원에게 그룹장 권한을 부여해주세요."),
    USER_GROUP_LEADER("409", HttpStatus.CONFLICT, "해당 유저는 그룹 내의 그룹장입니다. 그룹장은 내보낼 수 없습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    GroupErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }
}
