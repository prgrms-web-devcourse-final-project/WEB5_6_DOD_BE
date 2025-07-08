package com.grepp.spring.app.controller.api.event.payload;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleResultResponse {
    private String eventTitle;
    private Integer totalParticipants;
    private Recommendation recommendation;

    @Getter
    @Setter
    public static class TimeSlotDetail {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer participantCount;
        private List<Participant> participants;
        private Boolean isSelected; // 최종 선택된 시간대인지
        private String timeSlotId; // 시간대 식별자
    }

    @Getter
    @Setter
    public static class Participant {
        private String memberId;
        private String memberName;
    }

    @Getter
    @Setter
    public static class Recommendation {
        private TimeSlotDetail longestMeetingTime; // 가장 오래 만날 수 있는 시간
        private TimeSlotDetail earliestMeetingTime; // 가장 빨리 만날 수 있는 시간
    }
}
