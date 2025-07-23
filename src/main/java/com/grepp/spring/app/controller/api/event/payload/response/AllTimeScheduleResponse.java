package com.grepp.spring.app.controller.api.event.payload.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class AllTimeScheduleResponse {
    private final Long eventId;
    private final String eventTitle;
    private final String description;
    private final TimeTable timeTable;
    private final List<MemberSchedule> memberSchedules;
    private final Integer totalMembers;
    private final Integer confirmedMembers;
    private final Map<String, List<Integer>> participantCounts;

    @Getter
    @Builder
    public static class TimeTable {
        private final List<DateInfo> dates;
        private final String startTime;
        private final String endTime;
    }

    @Getter
    @Builder
    public static class DateInfo {
        private final String date;
        private final String dayOfWeek;
        private final String displayDate;
    }

    @Getter
    @Builder
    public static class MemberSchedule {
        private final String eventMemberId;
        private final String memberName;
        private final List<DailyTimeSlot> dailyTimeSlots;
        private final Boolean isConfirmed;
    }

    @Getter
    @Builder
    public static class DailyTimeSlot {
        private final String date;
        private final String timeBit;
    }
}