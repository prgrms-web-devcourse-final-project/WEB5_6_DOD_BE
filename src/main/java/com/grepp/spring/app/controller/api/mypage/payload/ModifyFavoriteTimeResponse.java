package com.grepp.spring.app.controller.api.mypage.payload;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyFavoriteTimeResponse {

  private List<ModifyFavTimeList> modifyFavTime;

  @Getter @Setter
  public static class ModifyFavTimeList{
    private Long favoriteTimeId;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime dateTime; // 필요없지 않나 흠. // TODO: 요일 어떻게 받을지 더 고민하고 수정하삼
    private DayOfWeek weekday;
    private LocalDateTime updatedAt;
  }


}
