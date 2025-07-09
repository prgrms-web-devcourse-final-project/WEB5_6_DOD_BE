package com.grepp.spring.app.controller.api.schedules.payload.request;

import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.SchedulesStatus;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ModifySchedulesRequest {
    private Long eventId; // 추가

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SchedulesStatus SchedulesStatus;
    private String location;
    private String specificLocation;
    private MeetingPlatform meetingPlatform;
    private String platformURL;

    // 온라인/오프라인 공통 워크 스페이스
    private Long workspaceId; // 추가
    private String workspaceName; // 추가
    private String workspaceUrl; // 추가
}
