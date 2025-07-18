package com.grepp.spring.app.controller.api.event.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "이벤트 상세 조회 응답")
public class ShowEventResponse {

    @Schema(description = "이벤트 ID", example = "12345")
    private Long eventId;

    @Schema(description = "이벤트 제목", example = "스터디 모임")
    private String title;

    @Schema(description = "이벤트 설명", example = "매주 화요일 스터디 모임입니다.")
    private String description;

    @Schema(description = "요청한 사용자의 역할", example = "ROLE_MASTER")
    private String role;

    @Schema(description = "그룹 ID", example = "6789")
    private Long groupId;
}