package com.grepp.spring.app.controller.api.mypage.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetCalendarSyncRequest {
  private boolean isSynced;
  private String accessToken;
  private String refreshToken;
  private Long calendarId;

}
