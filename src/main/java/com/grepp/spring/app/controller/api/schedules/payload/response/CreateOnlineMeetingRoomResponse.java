package com.grepp.spring.app.controller.api.schedules.payload.response;

import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOnlineMeetingRoomResponse {
    private MeetingPlatform meetingPlatform;
    private String workspaceUrl;
}
