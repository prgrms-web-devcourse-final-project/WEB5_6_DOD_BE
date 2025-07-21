package com.grepp.spring.infra.error.exceptions.mypage;

import com.grepp.spring.infra.response.MyPageErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CalendarAuthRequiredException extends RuntimeException {

  private final MyPageErrorCode code;

  private final String redirectUrl;

  public CalendarAuthRequiredException(MyPageErrorCode code, Exception ex) {
    this.code = code;
    this.redirectUrl = null; // 기본 null 처리
    log.error(ex.getMessage(), ex);
  }

  public CalendarAuthRequiredException(MyPageErrorCode code, String reauthUrl) {
    this.code = code;
    this.redirectUrl = reauthUrl;

  }
  public String getRedirectUrl() {
    return redirectUrl;
  }

  public MyPageErrorCode getCode() {
    return code;
  }


}
