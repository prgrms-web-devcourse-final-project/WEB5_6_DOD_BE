package com.grepp.spring.app.controller.api.schedule.payload.request;

import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOnlineMeetingRoomRequest {
    private MeetingPlatform meetingPlatform;
}
