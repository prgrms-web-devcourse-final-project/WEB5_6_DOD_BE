package com.grepp.spring.app.controller.api.mypage.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicCalendarIdResponse {

  private String calendarId; // 저장된 구글 공개 캘린더 ID

}
