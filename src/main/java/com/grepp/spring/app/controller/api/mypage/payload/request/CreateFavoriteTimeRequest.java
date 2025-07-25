package com.grepp.spring.app.controller.api.mypage.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFavoriteTimeRequest {

    private String timeBitMon;
    private String timeBitTue;
    private String timeBitWed;
    private String timeBitThu;
    private String timeBitFri;
    private String timeBitSat;
    private String timeBitSun;

}
