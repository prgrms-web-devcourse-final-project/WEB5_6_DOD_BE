package com.grepp.spring.app.controller.api.mypage.payload.request;

import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyFavoriteTimeRequest {

  private Long favoriteTimeId;
  private LocalTime startTime;
  private LocalTime endTime;
  private LocalDateTime weekday;

}
