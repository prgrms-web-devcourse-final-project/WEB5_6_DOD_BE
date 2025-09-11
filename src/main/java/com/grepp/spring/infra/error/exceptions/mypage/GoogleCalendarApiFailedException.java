package com.grepp.spring.infra.error.exceptions.mypage;

import com.grepp.spring.infra.error.exceptions.CustomException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleCalendarApiFailedException extends CustomException {

  public GoogleCalendarApiFailedException(MyPageErrorCode code) {
    super(code);
  }

  public GoogleCalendarApiFailedException(MyPageErrorCode code, Exception e) {
    super(code, e);
  }

}
