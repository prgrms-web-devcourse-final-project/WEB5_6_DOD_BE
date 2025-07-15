package com.grepp.spring.app.model.mypage.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleEventDto {
  private String googleEventId;  // 구글 이벤트 ID -> 구글에선 '일정'이 또 '이벤트'라네요
  private String title;          // summary
  private LocalDateTime start;
  private LocalDateTime end;
}
