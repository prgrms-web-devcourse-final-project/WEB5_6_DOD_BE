package com.grepp.spring.app.controller.api.event.payload;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllTimeScheduleResponse {
    private Long eventId;
    private String eventTitle;
    private TimeTable timeTable;
    private List<MemberSchedule> memberSchedules;
    private Integer totalMembers;
    private Integer confirmedMembers;

    @Getter
    @Setter
    public static class TimeTable {
        private List<String> dates;
        private String startTime;
        private String endTime;
    }

    @Getter
    @Setter
    public static class MemberSchedule {
        private String eventMemberId;
        private String memberName;
        private String role;
        private List<DailyTimeSlot> dailyTimeSlots;
        private Boolean isConfirmed;
    }

    @Getter
    @Setter
    public static class DailyTimeSlot {
        private LocalDate date;
        private String dayOfWeek; // "MON", "TUE", "WED", etc.
        private String displayDate; // "07-13" 형태
        private String timeBit; // 해당 멤버의 가능한 시간 비트
    }
}