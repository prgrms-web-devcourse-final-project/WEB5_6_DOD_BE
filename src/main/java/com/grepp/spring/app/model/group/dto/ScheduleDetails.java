package com.grepp.spring.app.model.group.dto;

import com.grepp.spring.app.model.event.code.MeetingType;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleDetails {
    private Long scheduleId;
    private String scheduleName;
    private MeetingType meetingType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private DayOfWeek weekDay;
    private ArrayList<String> memberNames;

}
