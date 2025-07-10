package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedules.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateScheduleDto {
    private Long eventId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ScheduleStatus scheduleStatus;
    private String scheduleName;
    private String description;
    private List<String> memberIds;

    public static CreateScheduleDto toDto(CreateSchedulesRequest request) {

        return CreateScheduleDto.builder()
            .eventId(request.getEventId())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .scheduleStatus(request.getScheduleStatus())
            .scheduleName(request.getScheduleName())
            .description(request.getDescription())
            .memberIds(request.getMemberIds()).build();
    }
}

