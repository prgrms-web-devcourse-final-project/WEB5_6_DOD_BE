package com.grepp.spring.infra.response;

import com.grepp.spring.infra.response.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ScheduleErrorCode implements ErrorCode {
    NOT_SCHEDULE_MASTER("404", HttpStatus.NOT_FOUND, "❌권한 이슈❌ ROLE_MASTER 계정만 가능한 작업입니다."),
    LOCATION_NOT_FOUND("404",HttpStatus.NOT_FOUND, "해당 투표리스트(장소)를 찾을 수 없습니다. locationId를 확인해주세요."),
    SCHEDULE_MEMBER_NOT_FOUND("404",HttpStatus.NOT_FOUND, "스케줄에서 회원을 찾을 수 없습니다."),
    WORKSPACE_NOT_FOUND("404",HttpStatus.NOT_FOUND,"워크스페이스를 찾을 수 없습니다."),
    EVENT_NOT_ACTIVATED("409", HttpStatus.CONFLICT, "이미 일정이 생성된 이벤트입니다."),
    NOT_SCHEDULE_MEMBER("403",HttpStatus.FORBIDDEN,"일정에 포함된 멤버만 조회할 수 있습니다."),
    VOTE_ALREADY_PROGRESS("409",HttpStatus.CONFLICT,"투표중입니다! 투표중에는 후보장소를 등록할 수 없습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ScheduleErrorCode(String code, HttpStatus status, String message) {
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
