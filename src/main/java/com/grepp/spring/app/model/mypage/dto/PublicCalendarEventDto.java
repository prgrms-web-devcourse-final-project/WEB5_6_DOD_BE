package com.grepp.spring.app.model.mypage.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicCalendarEventDto {

  private String eventId;
  private String title; // summary 랑 같은 역할
  private String start; // 시작 (date / dateTime)
  private String end;
  private boolean allDay;
  private String status;
//  private String htmlLink; // 구글 캘린더 원본 링크 -> 필요할까?
}



