package com.grepp.spring.app.controller.api.schedules.payload;

import com.grepp.spring.app.model.schedule.domain.MEETING_PLATFORM;
import com.grepp.spring.app.model.schedule.domain.SCHEDULES_STATUS;
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
    private SCHEDULES_STATUS SCHEDULES_STATUS;
    private String description;
    private List<String> memberIds;
}
