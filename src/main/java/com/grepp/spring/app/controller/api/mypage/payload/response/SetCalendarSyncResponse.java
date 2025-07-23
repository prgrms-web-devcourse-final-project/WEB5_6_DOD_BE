package com.grepp.spring.app.controller.api.mypage.payload.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SetCalendarSyncResponse {
  private boolean isSynced; // 동기화 on/off 여부
  private LocalDateTime syncUpdatedAt; // lastSyncedAt 에서 변경. 연동 끊었을 때 시간 반영 고려
  private String reauthUrl; // 재연동 할 때만 필요한 구글 OAuth 인증 URL. Nullable

}
