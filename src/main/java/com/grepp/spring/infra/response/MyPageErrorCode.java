package com.grepp.spring.infra.response;

import org.springframework.http.HttpStatus;

public enum MyPageErrorCode {
  INVALID_FAVORITE_REQUEST("400", HttpStatus.BAD_REQUEST, "잘못된 즐겨찾기 요청입니다."),
  INVALID_MEMBER_REQUEST("400", HttpStatus.BAD_REQUEST, "잘못된 회원 요청입니다."),
  INVALID_PUBLIC_CALENDAR_ID("400", HttpStatus.BAD_REQUEST, "유효하지 않은 공개 캘린더 ID 입니다."),
  AUTHENTICATION_REQUIRED("401", HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
  //GOOGLE_AUTH_FAILED("401", HttpStatus.UNAUTHORIZED, "구글 OAuth 인증에 실패했습니다."),
  // 토큰 없음으로 연동 불가
  //CALENDAR_AUTH_REQUIRED("401", HttpStatus.UNAUTHORIZED, "구글 캘린더 연동을 위해 재인증이 필요합니다."),
  // 토큰 만료 -> 자동 갱신 실패
  //CALENDAR_TOKEN_EXPIRED("401", HttpStatus.UNAUTHORIZED, "구글 토큰이 만료되어 재인증이 필요합니다."),
  // FAVORITE_FORBIDDEN("403", HttpStatus.FORBIDDEN, "해당 즐겨찾기에 대한 권한이 없습니다."),
  FAVORITE_NOT_FOUND("404", HttpStatus.NOT_FOUND, "즐겨찾기를 찾을 수 없습니다."),
  MEMBER_NOT_FOUND("404", HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
  PUBLIC_CALENDAR_ID_NOT_FOUND("404", HttpStatus.NOT_FOUND, "등록된 공개 캘린더 ID가 없습니다."),
  FAVORITE_ALREADY_EXISTS("409", HttpStatus.CONFLICT, "즐겨찾기가 이미 존재합니다."),
  // 구글 API 호출 실패 (토큰 교환 실패, 네트워크 문제 포함해서)
  //CALENDAR_SYNC_FAILED("500", HttpStatus.INTERNAL_SERVER_ERROR, "구글 캘린더 연동에 실패했습니다."),
  // 응답 파싱 실패
  //INVALID_CALENDAR_RESPONSE("500", HttpStatus.INTERNAL_SERVER_ERROR, "구글 캘린더 응답 데이터가 유효하지 않습니다."),
  //CALENDAR_EVENT_SAVE_FAILED("500", HttpStatus.INTERNAL_SERVER_ERROR, "구글 일정 저장 중 오류가 발생했습니다."),
  //TOKEN_SAVE_FAILED("500", HttpStatus.INTERNAL_SERVER_ERROR, "구글 인증 토큰 저장에 실패했습니다."),
  FAVORITE_SAVE_FAILED("500", HttpStatus.INTERNAL_SERVER_ERROR, "즐겨찾기 저장에 실패했습니다."),
  GOOGLE_CALENDAR_API_FAILED("503", HttpStatus.SERVICE_UNAVAILABLE, "구글 캘린더 API 호출에 실패했습니다.");

  private final String code;
  private final HttpStatus status;
  private final String message;


  MyPageErrorCode(String code, HttpStatus status, String message) {
    this.code = code;
    this.status = status;
    this.message = message;
  }


  public String code() {return code;}
  public HttpStatus status() {return status;}
  public String message() {return message;}

}

