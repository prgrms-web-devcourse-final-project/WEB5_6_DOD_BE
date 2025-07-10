package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShowScheduleDto {
    private Long id;
    private Long eventId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ScheduleStatus scheduleStatus;
    private String location;
    private String specificLocation;
    private String description;
    private MeetingPlatform meetingPlatform;
    private String platformUrl;
    private String scheduleName;

    private List<String> members;
    private List<String> workspaceUrls;
    private List<String> workspaceNames;

    public static ShowScheduleDto toDto(Long event, Schedule schedule, List<ScheduleMember> scheduleMembers, List<Workspace> workspace) {

           List<String> members = scheduleMembers.stream().map(ScheduleMember::getName).collect(Collectors.toList());

           List<String> workspacesUrls = workspace.stream().map(Workspace::getUrl).collect(Collectors.toList());

           List<String> workspacesNames = workspace.stream().map(Workspace::getName).collect(Collectors.toList());

           return ShowScheduleDto.builder()
               .id(schedule.getId())
               .eventId(event)
               .startTime(schedule.getStartTime())
               .endTime(schedule.getEndTime())
               .scheduleStatus(schedule.getStatus())
               .location(schedule.getLocation())
               .specificLocation(schedule.getSpecificLocation())
               .description(schedule.getDescription())
               .meetingPlatform(schedule.getMeetingPlatform())
               .platformUrl(schedule.getPlatformUrl())
               .scheduleName(schedule.getScheduleName())
               .members(members)
               .workspaceUrls(workspacesUrls)
               .workspaceNames(workspacesNames)
               .build();
    }
}

