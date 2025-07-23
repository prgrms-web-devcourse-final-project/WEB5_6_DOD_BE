package com.grepp.spring.app.controller.api.event.payload.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ScheduleResultResponse {
    private final String eventTitle;
    private final Integer totalParticipants;
    private final Recommendation recommendation;

    @Getter
    @Builder
    public static class TimeSlotDetail {
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;
        private final Integer participantCount;
        private final List<Participant> participants;
        private final Boolean isSelected; // 최종 선택된 시간대인지
        private final String timeSlotId; // 시간대 식별자
    }

    @Getter
    @Builder
    public static class Participant {
        private final String memberId;
        private final String memberName;
    }

    @Getter
    @Builder
    public static class Recommendation {
        private final List<TimeSlotDetail> longestMeetingTimes; // 가장 오래 만날 수 있는 시간들
        private final List<TimeSlotDetail> earliestMeetingTimes; // 가장 빨리 만날 수 있는 시간들
    }

}