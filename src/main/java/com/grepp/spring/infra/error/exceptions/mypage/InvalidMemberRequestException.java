package com.grepp.spring.infra.error.exceptions.mypage;

import com.grepp.spring.infra.response.MyPageErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidMemberRequestException extends RuntimeException {

  private final MyPageErrorCode code;

  public InvalidMemberRequestException(MyPageErrorCode code) {
    this.code = code;
  }

  public InvalidMemberRequestException(MyPageErrorCode code, Exception e) {
    this.code = code;
    log.error(e.getMessage(),e);
  }

  public MyPageErrorCode getCode() {
    return code;
  }


}
