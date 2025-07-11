package com.grepp.spring.app.controller.api.mypage.payload.response;

import com.grepp.spring.app.controller.api.mypage.payload.response.CreateFavoritePlaceResponse.FavoriteLocationList;
import com.grepp.spring.app.model.mypage.dto.FavoriteLocationDto;
import com.grepp.spring.app.model.mypage.dto.FavoriteTimetableDto;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFavoriteTimeResponse {

  private List<FavoriteTimeList> fav_times;

  @Getter @Setter
  public static class FavoriteTimeList{
    private Long favoriteTimeId;
    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek weekday;
    private LocalDateTime createdAt;
  }



}
