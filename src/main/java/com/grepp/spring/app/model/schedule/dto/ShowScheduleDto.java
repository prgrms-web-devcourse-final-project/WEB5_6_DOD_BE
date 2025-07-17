package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedules.payload.response.ShowScheduleResponse;
import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.WorkspaceType;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ShowScheduleDto {

    private Long id;
    private Long eventId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private MeetingType meetingType; // 온오프라인여부
    private String location;
    private String specificLocation;
    private String scheduleName;
    private String description;



    private MeetingPlatform meetingPlatform; // ZOOM | GOOGLE_MEET | NONE
    private String platformName;
    private String platformUrl;

    private List<String> members;
    private List<WorkspaceDto> workspaces;

    public static ShowScheduleResponse fromDto(ShowScheduleDto dto) {

        return ShowScheduleResponse.builder()
            .eventId(dto.getEventId())
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .meetingType(dto.getMeetingType())
            .location(dto.getLocation())
            .specificLocation(dto.getSpecificLocation())
            .scheduleName(dto.getScheduleName())
            .description(dto.getDescription())
            .meetingPlatform(dto.getMeetingPlatform())
            .platformName(dto.getPlatformName())
            .platformUrl(dto.getPlatformUrl())
            .members(dto.getMembers())
            .workspaces(dto.getWorkspaces())
            .build();
    }

    public static ShowScheduleDto fromEntity(MeetingType meetingType, Long event, Schedule schedule,
        List<ScheduleMember> scheduleMembers, List<Workspace> workspace) {

        List<String> members = scheduleMembers.stream().map(ScheduleMember::getName)
            .collect(Collectors.toList());


        List<WorkspaceType> workspacesType = workspace.stream().map(Workspace::getType)
            .collect(Collectors.toList());

        List<String> workspacesNames = workspace.stream().map(Workspace::getName)
            .collect(Collectors.toList());

        List<String> workspacesUrls = workspace.stream().map(Workspace::getUrl)
            .collect(Collectors.toList());

        List<WorkspaceDto> workspaces = IntStream.range(0, workspace.size())
            .mapToObj(i-> new WorkspaceDto(
                workspace.get(i).getId(),
                workspacesType.get(i),
                workspacesNames.get(i),
                workspacesUrls.get(i)
            ))
            .collect(Collectors.toList());

        return ShowScheduleDto.builder()
            .id(schedule.getId())
            .eventId(event)
            .startTime(schedule.getStartTime())
            .endTime(schedule.getEndTime())
            .meetingType(meetingType)
            .location(schedule.getLocation())
            .specificLocation(schedule.getSpecificLocation())
            .description(schedule.getDescription())
            .meetingPlatform(schedule.getMeetingPlatform())
            .platformUrl(schedule.getPlatformUrl())
            .scheduleName(schedule.getScheduleName())
            .members(members)
            .workspaces(workspaces)
            .build();
    }
}

