package com.grepp.spring.app.controller.api.schedules.payload.request;

import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.schedule.code.MEETING_PLATFORM;
import com.grepp.spring.app.model.schedule.code.SCHEDULES_STATUS;
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

    private MeetingType onOffline; // 추가
    private Long workspaceId; // 추가
    private String workspaceUrl; // 추가
}
