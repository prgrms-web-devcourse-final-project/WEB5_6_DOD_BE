package com.grepp.spring.app.controller.api.schedules.payload.response;

import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.dto.ShowScheduleDto;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShowScheduleResponse {
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
    private List<String> workspacesName;
    private List<String> workspacesUrl;

    public static ShowScheduleResponse fromDto(ShowScheduleDto dto) {

        return ShowScheduleResponse.builder()
            .eventId(dto.getEventId())
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .scheduleStatus(dto.getScheduleStatus())
            .location(dto.getLocation())
            .specificLocation(dto.getSpecificLocation())
            .description(dto.getDescription())
            .meetingPlatform(dto.getMeetingPlatform())
            .members(dto.getMembers())
            .workspacesName(dto.getWorkspaceNames())
            .workspacesUrl(dto.getWorkspaceUrls())
            .scheduleName(dto.getScheduleName())
            .build();
    }

}
