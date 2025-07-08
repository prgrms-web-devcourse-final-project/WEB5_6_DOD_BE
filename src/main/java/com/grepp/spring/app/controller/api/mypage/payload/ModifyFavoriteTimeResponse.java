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
    private LocalDateTime dateTime;
    private DayOfWeek weekday;
    private LocalDateTime updatedAt;
  }


}
