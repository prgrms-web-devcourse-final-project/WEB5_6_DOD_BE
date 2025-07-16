package com.grepp.spring.app.controller.api.event.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "이벤트 생성 응답")
public class CreateEventResponse {

    @Schema(description = "생성된 이벤트 ID", example = "1")
    private Long eventId;

    @Schema(description = "이벤트 제목", example = "스터디 모임")
    private String title;

    @Schema(description = "그룹 ID", example = "10000")
    private Long groupId;
}