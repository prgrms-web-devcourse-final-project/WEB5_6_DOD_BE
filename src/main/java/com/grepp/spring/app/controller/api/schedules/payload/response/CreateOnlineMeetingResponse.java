package com.grepp.spring.app.controller.api.schedules.payload.response;

import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.Workspace;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOnlineMeetingResponse {
    private MeetingPlatform meetingPlatformCreate;
    private Workspace workspace;
    private String workspaceUrl;
}
