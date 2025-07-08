package com.grepp.spring.app.controller.api.event.payload;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyTimeScheduleResponse {
    private Long eventMemberId;
    private String memberName;
    private List<DailyTimeSlot> dailyTimeSlots;
    private Boolean isConfirmed;
    private LocalDateTime createdAt;

    @Getter
    @Setter
    public static class DailyTimeSlot {
        private LocalDate date;
        private String dayOfWeek; // "MON", "TUE", "WED", ...
        private String displayDate; // "07/13" 형태
        private Long timeBit; // 비트로 30분 단위 시간 표현
        private List<TimeSlotInfo> timeSlotInfos;
    }

    @Getter
    @Setter
    public static class TimeSlotInfo {
        private Integer slotIndex; // 0~47 (00:00~23:30)
        private String timeLabel; // "09:00", "09:30", ...
        private Boolean isSelected;
    }
}
