package com.grepp.spring.app.controller.api.mainpage.payload;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ShowMainPageRequest {
  // 그룹 리스트 조회
  private String memberId;

  // 일정 조회
  private Long calendarId;

  // 캘린더 조회
  private String yearMonth; // "year-month" 형태로 나오게끔, 서비스 단에서 변환해서 사용할 예정

}
