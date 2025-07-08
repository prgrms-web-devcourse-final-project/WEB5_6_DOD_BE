package com.grepp.spring.app.controller.api.event.payload;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateScheduleResultResponse {
    private Long eventId;
    private String eventTitle;
    private Integer totalParticipants;
    private List<TimeSlotSummary> timeSlotSummaries;
    private RecommendedSlots recommendedSlots;
    private String status;
    private LocalDateTime createdAt;

    @Getter
    @Setter
    public static class TimeSlotSummary {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer participantCount;
        private List<String> participantNames;
        private String displayTime; // "2025년 7월 13일 (금) 18:00 ~22:00" 형태
    }

    @Getter
    @Setter
    public static class RecommendedSlots {
        private TimeSlotSummary longestMeetingTime; // 가장 오래 만날 수 있는 시간
        private TimeSlotSummary earliestMeetingTime; // 가장 빨리 만날 수 있는 시간
    }
}