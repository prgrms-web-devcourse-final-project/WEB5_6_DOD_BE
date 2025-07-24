package com.grepp.spring.app.controller.api.mypage.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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




