package com.grepp.spring.infra.error.exceptions.mypage;

import com.grepp.spring.infra.response.MyPageErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CalendarEventSaveFailedException extends RuntimeException {

  private final MyPageErrorCode code;

  public CalendarEventSaveFailedException(MyPageErrorCode code) {
    this.code = code;
  }

  public CalendarEventSaveFailedException(MyPageErrorCode code, Exception e) {
    this.code = code;
    log.error(e.getMessage(),e);
  }

  public MyPageErrorCode getCode() {
    return code;
  }


}
