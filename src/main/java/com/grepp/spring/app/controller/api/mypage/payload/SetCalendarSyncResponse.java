package com.grepp.spring.app.controller.api.mypage.payload;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetCalendarSyncResponse {
  private boolean isSynced; // 동기화 on/off 여부
  private LocalDateTime lastSyncAt; // 마지막 동기화 시점

}
