package com.grepp.spring.app.controller.api.event.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import java.time.LocalDate;

@Getter
@Setter
public class MyTimeScheduleRequest {

    @NotNull
    private List<DailyTimeSlot> dailyTimeSlots;
    @NotNull
    private String timezone;

    @Getter
    @Setter
    public static class DailyTimeSlot {
        @NotNull
        private LocalDate date;

        @NotNull
        private String timeBit; // 비트로 30분 단위 시간 표현 (0~47: 00:00~23:30)
    }
}
