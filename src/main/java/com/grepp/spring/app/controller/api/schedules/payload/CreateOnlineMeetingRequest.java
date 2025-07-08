package com.grepp.spring.app.controller.api.schedules.payload;

import com.grepp.spring.app.model.schedule.domain.MEETING_PLATFORM;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOnlineMeetingRequest {
    private MEETING_PLATFORM meetingPlatform;
}
