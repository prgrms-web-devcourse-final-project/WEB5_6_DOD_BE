package com.grepp.spring.app.controller.api.mypage.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarSyncStatusResponse {
  private Long id;
  private String name;
  private boolean synced;

}
