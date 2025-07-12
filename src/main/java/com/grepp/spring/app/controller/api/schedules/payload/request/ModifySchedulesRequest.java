package com.grepp.spring.app.controller.api.schedules.payload.request;

import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.code.WorkspaceType;
import com.grepp.spring.app.model.schedule.dto.WorkspaceDto;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ModifySchedulesRequest {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String ScheduleName;
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

}
