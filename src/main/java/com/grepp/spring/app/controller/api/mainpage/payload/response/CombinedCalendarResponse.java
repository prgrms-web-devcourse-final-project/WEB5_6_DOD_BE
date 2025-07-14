package com.grepp.spring.app.controller.api.mainpage.payload.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedCalendarResponse {
  private String name;
  private boolean synced;
  private List<GoogleCalendarDto> googleCalendars;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GoogleCalendarDto {
    private String id; // 구글 캘린더 ID (API 상에서 식별용)
    private String name;
  }

}
