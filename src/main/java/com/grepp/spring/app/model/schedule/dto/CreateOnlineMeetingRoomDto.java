package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedule.payload.response.CreateOnlineMeetingRoomResponse;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateOnlineMeetingRoomDto {
    private MeetingPlatform meetingPlatform;
    private String workspaceUrl;

    public static CreateOnlineMeetingRoomDto toDto(String url) {
        return CreateOnlineMeetingRoomDto.builder()
            .meetingPlatform(MeetingPlatform.ZOOM)
            .workspaceUrl(url)
            .build();
    }

    public static CreateOnlineMeetingRoomResponse fromDto(CreateOnlineMeetingRoomDto dto) {
        return CreateOnlineMeetingRoomResponse.builder()
            .meetingPlatform(dto.meetingPlatform)
            .workspaceUrl(dto.workspaceUrl)
            .build();
    }
}
