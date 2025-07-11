package com.grepp.spring.app.controller.api.schedules.payload.response;

import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.dto.ShowScheduleDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private Long eventId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location; // 중간장소
    private String specificLocation; // 상세장소
    private String description;
    private String scheduleName;

    private MeetingPlatform meetingPlatform; // ZOOM | GOOGLE_MEET | NONE
    private String platformUrl;

    private List<String> members;
    private Map<String, String> workspaces; // (workspacesName , workspacesUrl)

//    public static ShowScheduleResponse fromDto(ShowScheduleDto dto) {
//
//        return ShowScheduleResponse.builder()
//            .eventId(dto.getEventId())
//            .startTime(dto.getStartTime())
//            .endTime(dto.getEndTime())
//            .location(dto.getLocation())
//            .specificLocation(dto.getSpecificLocation())
//            .scheduleName(dto.getScheduleName())
//            .description(dto.getDescription())
//            .meetingPlatform(dto.getMeetingPlatform())
//            .members(dto.getMembers())
//            .workspaces(dto.getWorkspaces())
//            .build();
//    }
}
