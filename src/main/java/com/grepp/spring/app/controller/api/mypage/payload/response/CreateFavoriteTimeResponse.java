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

  private String timeBitMon;
  private String timeBitTue;
  private String timeBitWed;
  private String timeBitThu;
  private String timeBitFri;
  private String timeBitSat;
  private String timeBitSun;
}




