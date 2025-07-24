package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedule.payload.request.ModifySchedulesRequest;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
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

    private Double specificLatitude; // 추가
    private Double specificLongitude;  // 추가

    private MeetingPlatform meetingPlatform;
    private String platformURL;
    private ScheduleStatus status;

    // 온라인/오프라인 공통 워크 스페이스
    private Long workspaceId; // 추가
    private List<ModifyWorkspaceDto> workspace;

    public static ModifyScheduleDto toDto(ModifySchedulesRequest request) {

        return ModifyScheduleDto.builder()
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .scheduleName(request.getScheduleName())
            .description(request.getDescription())
            .location(request.getLocation())
            .specificLocation(request.getSpecificLocation())
            .specificLatitude(request.getSpecificLatitude()) // 추가
            .specificLongitude(request.getSpecificLongitude()) // 추가
            .meetingPlatform(request.getMeetingPlatform())
            .platformURL(request.getPlatformURL())
            .status(request.getStatus())
            .workspaceId(request.getWorkspaceId())
            .workspace(request.getWorkspace())
            .build();
    }
}
