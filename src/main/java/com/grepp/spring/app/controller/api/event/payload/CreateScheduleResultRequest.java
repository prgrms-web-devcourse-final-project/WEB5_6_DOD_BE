package com.grepp.spring.app.controller.api.event.payload;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateScheduleResultRequest {

    @NotNull
    private List<TimeSlotResult> timeSlotResults;

    private String location;
    private String specificLocation;
    private String description;

    @Getter
    @Setter
    public static class TimeSlotResult {
        @NotNull
        private LocalDateTime startTime;

        @NotNull
        private LocalDateTime endTime;

        @NotNull
        private List<String> participantNames;

        private Boolean isSelected;
    }
}
