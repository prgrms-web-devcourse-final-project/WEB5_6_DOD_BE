package com.grepp.spring.app.controller.api.event.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AllTimeScheduleResponse {
    private Long eventId;
    private String eventTitle;
    private String description;
    private TimeTable timeTable;
    private List<MemberSchedule> memberSchedules;
    private Integer totalMembers;
    private Integer confirmedMembers;
    private Map<String, List<Integer>> participantCounts;

    @Getter
    @Setter
    public static class TimeTable {
        private List<DateInfo> dates;
        private String startTime;
        private String endTime;
    }

    @Getter
    @Setter
    public static class DateInfo {
        private String date;
        private String dayOfWeek;
        private String displayDate;
    }

    @Getter
    @Setter
    public static class MemberSchedule {
        private String eventMemberId;
        private String memberName;
        private List<DailyTimeSlot> dailyTimeSlots;
        private Boolean isConfirmed;
    }

    @Getter
    @Setter
    public static class DailyTimeSlot {
        private String date;
        private String timeBit;
    }
}