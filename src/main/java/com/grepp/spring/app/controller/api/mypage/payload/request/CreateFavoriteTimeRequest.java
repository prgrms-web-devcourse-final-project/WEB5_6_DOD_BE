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

    @Schema(example = "14:00")
    @NotNull
    private LocalTime startTime;
    @Schema(example = "15:00")
    @NotNull
    private LocalTime endTime;
    @Schema(example = "MONDAY")
    @NotNull
    private DayOfWeek weekday;


}
