package com.grepp.spring.app.controller.api.schedules.payload.request;

import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSchedulesRequest {
    private Long eventId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ScheduleStatus scheduleStatus; // 추천/픽스/complete/NONE
    private MeetingType meetingType; // 온라인인지 오프라인인지

    private String scheduleName;            // 추가
    private String description;

    private int maxNumber; // 추가

    private Map<String, String> memberRoles;
}
