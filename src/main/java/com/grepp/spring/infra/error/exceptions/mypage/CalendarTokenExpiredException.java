package com.grepp.spring.infra.error.exceptions.mypage;

import com.grepp.spring.infra.response.MyPageErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CalendarTokenExpiredException extends RuntimeException {

  private final MyPageErrorCode code;
  private final String redirectUrl;

  public CalendarTokenExpiredException(MyPageErrorCode code) {
    this.code = code;
    this.redirectUrl = null;
  }

  public CalendarTokenExpiredException(MyPageErrorCode code, Exception ex) {
    this.code = code;
    this.redirectUrl = null;
    log.error(ex.getMessage(), ex);
  }

  public CalendarTokenExpiredException(MyPageErrorCode code, String redirectUrl) {
    this.code = code;
    this.redirectUrl = redirectUrl;
  }

  public MyPageErrorCode getCode() {
    return code;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }


}
