package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedules.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateScheduleDto {
    private Long eventId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ScheduleStatus scheduleStatus; // 추천/픽스/complete/NONE
    private MeetingType meetingType; // 온라인인지 오프라인인지

    private String scheduleName;
    private String description;

    private int maxNumber; // 추가

    private List<String> memberIds;
    private List<String> memberRoles;

    public static CreateScheduleDto toDto(CreateSchedulesRequest request) {

        Map<String, String> memberRoles = request.getMemberRoles();

        List<String> memberIds = new ArrayList<>(memberRoles.keySet());  // 키
        List<String> roles = new ArrayList<>(memberRoles.values());      // 값

        return CreateScheduleDto.builder()
            .eventId(request.getEventId())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .scheduleStatus(request.getScheduleStatus())
            .meetingType(request.getMeetingType())
            .scheduleName(request.getScheduleName())
            .description(request.getDescription())
            .maxNumber(request.getMaxNumber())
            .memberIds(memberIds)
            .memberRoles(roles).build();
    }
}

