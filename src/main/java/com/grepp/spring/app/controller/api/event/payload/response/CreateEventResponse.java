package com.grepp.spring.app.controller.api.event.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "이벤트 생성 응답")
public class CreateEventResponse {

    @Schema(description = "생성된 이벤트 ID", example = "1")
    @NotNull
    private final Long eventId;

    @Schema(description = "이벤트 제목", example = "스터디 모임")
    @NotNull
    private final String title;

    @Schema(description = "그룹 ID", example = "10000")
    @NotNull
    private final Long groupId;
}