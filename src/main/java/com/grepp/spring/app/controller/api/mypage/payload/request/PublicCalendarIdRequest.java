package com.grepp.spring.app.controller.api.mypage.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublicCalendarIdRequest {
  private String publicCalendarId; // 공개 캘린더 아이디

}
