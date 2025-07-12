package com.grepp.spring.app.controller.api.mypage.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFavoriteTimeRequest {

    private String timeBitMon;
    private String timeBitTue;
    private String timeBitWed;
    private String timeBitThu;
    private String timeBitFri;
    private String timeBitSat;
    private String timeBitSun;

}
