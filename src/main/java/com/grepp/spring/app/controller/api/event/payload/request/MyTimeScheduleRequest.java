package com.grepp.spring.app.controller.api.event.payload.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MyTimeScheduleRequest {

    @NotNull(message = "dailyTimeSlots는 필수입니다")
    @NotEmpty(message = "최소 1개의 시간 슬롯이 필요합니다")
    @Valid
    private List<DailyTimeSlot> dailyTimeSlots;

    @NotNull(message = "timezone은 필수입니다")
    @Pattern(regexp = "^[A-Za-z]+/[A-Za-z_]+$|^UTC[+-]\\d{1,2}$|^[A-Z]{3,4}$",
        message = "올바른 timezone 형식이어야 합니다 (예: Asia/Seoul, UTC+9)")
    private String timezone;

    @Getter
    @Setter
    public static class DailyTimeSlot {
        @NotNull(message = "날짜는 필수입니다")
        private LocalDate date;

        @NotNull(message = "timeBit은 필수입니다")
        private String timeBit;
    }
}