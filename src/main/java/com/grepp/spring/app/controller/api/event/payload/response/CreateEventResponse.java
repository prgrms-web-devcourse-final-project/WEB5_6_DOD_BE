package com.grepp.spring.app.controller.api.event.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이벤트 생성 응답")
public class CreateEventResponse {

    @Schema(description = "생성된 이벤트 ID", example = "12345")
    private Long eventId;

    @Schema(description = "이벤트 제목", example = "스터디 모임")
    private String title;

    public static CreateEventResponse of(Long eventId, String title) {
        CreateEventResponse response = new CreateEventResponse();
        response.setEventId(eventId);
        response.setTitle(title);
        return response;
    }
}