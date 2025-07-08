package com.grepp.spring.app.controller.api.schedules.payload;

import com.grepp.spring.app.model.schedule.domain.MEETING_PLATFORM;
import com.grepp.spring.app.model.schedule.domain.ON_OFFLINE;
import com.grepp.spring.app.model.schedule.domain.SCHEDULES_STATUS;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ModifySchedulesRequest {
    private Long eventId; // 추가

    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SCHEDULES_STATUS SCHEDULES_STATUS;
    private String location;
    private String specificLocation;
    private MEETING_PLATFORM meetingPlatform;
    private String platformURL;

    private ON_OFFLINE onOffline; // 추가
    private Long workspaceId; // 추가
    private String workspaceUrl; // 추가
}
