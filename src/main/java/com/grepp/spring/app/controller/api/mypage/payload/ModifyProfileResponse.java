package com.grepp.spring.app.controller.api.mypage.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyProfileResponse {
  private String memberId;
  private String profileImageNumber;
  private String name;
}
