package com.grepp.spring.app.controller.api.schedules.payload;

import com.grepp.spring.app.model.schedule.domain.MEETING_PLATFORM;
import com.grepp.spring.app.model.schedule.domain.SCHEDULES_STATUS;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShowSchedulesResponse {
    private Long eventId; // 추가
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SCHEDULES_STATUS SCHEDULES_STATUS;
    private String location;
    private String specificLocation;
    private String description;
    private MEETING_PLATFORM meetingPlatform;
    private String platformUrl;

    private List<String> members; // 추가
    private List<String> workspacesUrl; //추가
    private List<String> workspacesName; //추가

}
