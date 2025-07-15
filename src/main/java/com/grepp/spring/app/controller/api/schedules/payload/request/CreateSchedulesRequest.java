package com.grepp.spring.app.controller.api.schedules.payload.request;

import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.dto.ScheduleMemberRolesDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSchedulesRequest {
    private Long eventId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String scheduleName;
    private String description;

    private ScheduleStatus schedulesStatus;
    private MeetingType meetingType;

//    private int maxNumber; // 추가

    private List<ScheduleMemberRolesDto> memberRoles;
}
