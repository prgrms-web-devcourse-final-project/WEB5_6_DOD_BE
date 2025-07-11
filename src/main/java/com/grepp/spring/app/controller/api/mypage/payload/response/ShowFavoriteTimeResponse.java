package com.grepp.spring.app.controller.api.mypage.payload.response;


import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowFavoriteTimeResponse {

  private List<ShowFavoriteTimeResponse.FavoriteTimeList> fav_times;

  @Getter @Setter
  public static class FavoriteTimeList{
    private Long favoriteTimeId;
    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek weekday;
    private LocalDateTime createdAt;
  }

}
