package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedule.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.controller.api.schedule.payload.response.CreateSchedulesResponse;
import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateScheduleDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private ScheduleStatus scheduleStatus; // 추천/픽스/complete/NONE
    private MeetingType meetingType; // 온라인인지 오프라인인지

    private String scheduleName;
    private String description;

    private List<ScheduleMemberRolesDto> memberRoles;

    public static CreateScheduleDto toDto(CreateSchedulesRequest request) {

        return CreateScheduleDto.builder()
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .scheduleStatus(request.getSchedulesStatus())
            .meetingType(request.getMeetingType())
            .scheduleName(request.getScheduleName())
            .description(request.getDescription())
//            .maxNumber(request.getMaxNumber())
            .memberRoles(request.getMemberRoles()).build();
    }

    public static Schedule fromDto(CreateScheduleDto dto, Event id) {
        return Schedule.builder()
            .event(id)
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .status(dto.getScheduleStatus()) // 이벤트 -> 일정 생성 , 일정 픽스 2개의 상태가 있다. 생성 시 무조건 NONE으로 고정불가
            .scheduleName(dto.getScheduleName())
            .description(dto.getDescription()).build();
    }

    public static CreateSchedulesResponse toResponse(Long scheduleId) {
        return CreateSchedulesResponse.builder()
            .scheduleId(scheduleId)
            .build();
    }
}