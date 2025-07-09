package com.grepp.spring.app.controller.api.schedules.payload.request;

import com.grepp.spring.app.model.schedule.code.SchedulesStatus;
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
    private SchedulesStatus SchedulesStatus;
    private String description;

    private int maxNumber; // 추가

    private Map<String, String> memberRoles;
}
