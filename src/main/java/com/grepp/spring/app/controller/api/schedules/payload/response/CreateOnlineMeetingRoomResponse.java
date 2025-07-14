package com.grepp.spring.app.controller.api.schedules.payload.response;

import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOnlineMeetingRoomResponse {
    private MeetingPlatform meetingPlatform;
    private String workspaceUrl;
}
