package com.grepp.spring.infra.response;

import org.springframework.http.HttpStatus;

public enum ResponseCode {
    OK("200", HttpStatus.OK, "정상적으로 완료되었습니다."),
    ACTIVATED("200", HttpStatus.OK, "계정이 재활성화되었습니다."),
    CREATED("201", HttpStatus.CREATED, "정상적으로 생성되었습니다."),
    CONFLICT_REGISTER("409", HttpStatus.CONFLICT, "이미 가입된 회원입니다."),
    NOT_FOUND_MEMBER("404", HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    CONFLICT_TEL("409", HttpStatus.CONFLICT, "이미 등록된 번호입니다."),
    INVALID_TOKEN("401", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    ADMIN_WITHDRAWAL_NOT_ALLOWED("403", HttpStatus.FORBIDDEN, "그룹의 관리자는 탈퇴할 수 없습니다."),
    BAD_REQUEST("400", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_FILENAME("400", HttpStatus.BAD_REQUEST, "사용 할 수 없는 파일 이름입니다."),
    UNAUTHORIZED("401", HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    BAD_CREDENTIAL("401", HttpStatus.UNAUTHORIZED, "아이디나 비밀번호가 틀렸습니다."),
    NOT_FOUND("404", HttpStatus.NOT_FOUND, "NOT FOUND"),
    NOT_EXIST_PRE_AUTH_CREDENTIAL("401", HttpStatus.OK, "사전 인증 정보가 요청에서 발견되지 않았습니다."),
    INTERNAL_SERVER_ERROR("500", HttpStatus.INTERNAL_SERVER_ERROR, "서버에러 입니다."),
    SECURITY_INCIDENT("600", HttpStatus.OK, "비정상적인 로그인 시도가 감지되었습니다.");
    
    private final String code;
    private final HttpStatus status;
    private final String message;
    
    ResponseCode(String code, HttpStatus status, String message) {
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
