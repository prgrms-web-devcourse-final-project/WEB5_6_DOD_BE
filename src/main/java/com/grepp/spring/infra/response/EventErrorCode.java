package com.grepp.spring.infra.response;

import com.grepp.spring.infra.response.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum EventErrorCode implements ErrorCode {
    INVALID_EVENT_DATA("400", HttpStatus.BAD_REQUEST, "잘못된 이벤트 데이터입니다."),
    ALREADY_CONFIRMED_SCHEDULE("400", HttpStatus.BAD_REQUEST, "이미 확정된 일정입니다."),
    CANNOT_COMPLETE_EMPTY_SCHEDULE("400", HttpStatus.BAD_REQUEST, "빈 일정은 확정할 수 없습니다."),
    EVENT_MEMBER_LIMIT_EXCEEDED("400", HttpStatus.BAD_REQUEST, "이벤트 참여 인원이 초과되었습니다."),
    CANNOT_CREATE_SCHEDULE_RESULT("400", HttpStatus.BAD_REQUEST, "일정 조율 결과를 생성할 수 없습니다."),
    AUTHENTICATION_REQUIRED("401", HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    NOT_EVENT_MEMBER("403", HttpStatus.FORBIDDEN, "해당 이벤트의 참여자가 아닙니다."),
    NOT_GROUP_MEMBER("403", HttpStatus.FORBIDDEN, "해당 그룹의 멤버가 아닙니다."),
    EVENT_NOT_FOUND("404", HttpStatus.NOT_FOUND, "존재하지 않는 이벤트입니다."),
    MEMBER_NOT_FOUND("404", HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    GROUP_NOT_FOUND("404", HttpStatus.NOT_FOUND, "존재하지 않는 그룹입니다."),
    SCHEDULE_RESULT_NOT_FOUND("404", HttpStatus.NOT_FOUND, "일정 조율 결과를 찾을 수 없습니다."),
    ALREADY_JOINED_EVENT("409", HttpStatus.CONFLICT, "이미 참여한 이벤트입니다."),
    EVENT_ALREADY_COMPLETED("409", HttpStatus.CONFLICT, "이미 일정이 생성된 이벤트입니다."),
    INVALID_CANDIDATE_DATES("422", HttpStatus.UNPROCESSABLE_ENTITY, "유효하지 않은 후보 날짜입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    EventErrorCode(String code, HttpStatus status, String message) {
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