package com.grepp.spring.app.controller.api.mainpage.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarResponse {
  private String name;
  private boolean synced;

}
