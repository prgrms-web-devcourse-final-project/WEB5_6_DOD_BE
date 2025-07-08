package com.grepp.spring.app.controller.api.schedules.payload;

import com.grepp.spring.app.model.schedule.domain.MEETING_PLATFORM;
import com.grepp.spring.app.model.schedule.domain.WORKSPACE;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOnlineMeetingResponse {
    private MEETING_PLATFORM meetingPlatformCreate;
    private WORKSPACE workspace;
    private String workspaceUrl;
}
