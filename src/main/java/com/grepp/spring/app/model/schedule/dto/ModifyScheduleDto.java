package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedules.payload.request.ModifySchedulesRequest;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ModifyScheduleDto {
//    private Long eventId; // 추가

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String scheduleName;
    private String description;
    private String location;
    private String specificLocation;
    private MeetingPlatform meetingPlatform;
    private String platformName;
    private String platformURL;
    private ScheduleStatus status;

    // 온라인/오프라인 공통 워크 스페이스
    private Long workspaceId; // 추가
    private String workspaceName; // 추가
    private String workspaceUrl; // 추가

    public static ModifyScheduleDto toDto(ModifySchedulesRequest request) {
        return ModifyScheduleDto.builder()
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .scheduleName(request.getScheduleName())
            .description(request.getDescription())
            .location(request.getLocation())
            .specificLocation(request.getSpecificLocation())
            .meetingPlatform(request.getMeetingPlatform())
            .platformName(request.getPlatformName())
            .platformURL(request.getPlatformURL())
            .status(request.getStatus())
            .workspaceId(request.getWorkspaceId())
            .workspaceName(request.getWorkspaceName())
            .workspaceUrl(request.getWorkspaceUrl())
            .build();
    }

    public static Schedule fromDto(ModifyScheduleDto dto) {
        return Schedule.builder()
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .scheduleName(dto.getScheduleName())
            .description(dto.getDescription())
            .location(dto.getLocation())
            .specificLocation(dto.getSpecificLocation())
            .meetingPlatform(dto.getMeetingPlatform())
            .platformName(dto.getPlatformName())
            .platformUrl(dto.getPlatformURL())
            .status(dto.getStatus())
//            .workspaceId(dto.getWorkspaceId())
//            .workspaceName(dto.getWorkspaceName())
//            .workspaceUrl(dto.getWorkspaceUrl())
            .build();
    }
}
