package com.grepp.spring.app.model.mypage.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class GoogleEventDto {
  // 구글 이벤트 ID -> 구글에선 '일정'이 또 '이벤트'라네요
  private String googleEventId;     // 구글 이벤트 ID (event.id)
  private String title;             // summary
  private LocalDateTime start;      // dateTime → LocalDateTime
  private LocalDateTime end;        // dateTime → LocalDateTime
  private boolean allDay;           // start.date만 있고 dateTime 없으면 true
  private String etag;

  public GoogleEventDto(String googleEventId, String title, LocalDateTime start, LocalDateTime end, boolean allDay, String etag) {
    this.googleEventId = googleEventId;
    this.title = title;
    this.start = start;
    this.end = end;
    this.allDay = allDay;
    this.etag = etag;
  }// event.etag
}
