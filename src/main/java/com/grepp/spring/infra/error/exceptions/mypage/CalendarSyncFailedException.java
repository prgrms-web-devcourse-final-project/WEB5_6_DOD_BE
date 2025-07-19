package com.grepp.spring.infra.error.exceptions.mypage;

import com.grepp.spring.infra.response.MyPageErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CalendarSyncFailedException extends RuntimeException {

  private final MyPageErrorCode code;

  public CalendarSyncFailedException(MyPageErrorCode code) {
    this.code = code;
  }

  public CalendarSyncFailedException(MyPageErrorCode code, Exception e) {
    this.code = code;
    log.error(e.getMessage(),e);
  }

  public MyPageErrorCode getCode() {
    return code;
  }


}
