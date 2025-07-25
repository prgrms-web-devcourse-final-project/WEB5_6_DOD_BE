package com.grepp.spring.app.controller.api.schedule.payload.response;

import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.dto.ScheduleMembersDto;
import com.grepp.spring.app.model.schedule.dto.WorkspaceDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShowScheduleResponse {
    private ScheduleStatus scheduleStatus;
    private Long eventId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location; // 중간장소
    private String specificLocation; // 상세장소

    private Double specificLatitude; // 추가
    private Double specificLongitude; // 추가

    private String scheduleName;
    private String description;

    private MeetingType meetingType; // 온오프라인여부

    private MeetingPlatform meetingPlatform; // ZOOM | GOOGLE_MEET | NONE
    private String platformUrl;

    private List<ScheduleMembersDto> members;
    private List<WorkspaceDto> workspaces;

}
