package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedules.payload.request.ModifySchedulesRequest;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.code.WorkspaceType;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ModifyScheduleDto {
    private Long scheduleId; // 추가

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
    private List<WorkspaceDto> workspaces;

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
            .workspaces(request.getWorkspaces())
            .build();
    }
}
