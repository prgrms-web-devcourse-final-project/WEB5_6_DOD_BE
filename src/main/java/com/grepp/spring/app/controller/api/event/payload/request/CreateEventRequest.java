package com.grepp.spring.app.controller.api.event.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이벤트 생성 요청")
public class CreateEventRequest {

    @Schema(description = "이벤트 제목", example = "스터디 모임")
    @NotBlank(message = "이벤트 제목은 필수입니다.")
    @Size(max = 20, message = "이벤트 제목은 10자를 초과할 수 없습니다.")
    private String title;

    @Schema(description = "이벤트 설명", example = "매주 화요일 스터디 모임입니다.")
    @Size(max = 50, message = "이벤트 설명은 50자를 초과할 수 없습니다.")
    private String description;

    @Schema(description = "미팅 타입", example = "ONLINE", allowableValues = {"ONLINE", "OFFLINE"})
    @NotBlank(message = "미팅 타입은 필수입니다.")
    @Pattern(regexp = "^(ONLINE|OFFLINE)$", message = "미팅 타입은 ONLINE 또는 OFFLINE이어야 합니다.")
    private String meetingType;

    @Schema(description = "최대 참여 인원", example = "10")
    @NotNull(message = "최대 참여 인원은 필수입니다.")
    @Min(value = 1, message = "최대 참여 인원은 1명 이상이어야 합니다.")
    @Max(value = 100, message = "최대 참여 인원은 100명을 초과할 수 없습니다.")
    private Integer maxMember;

    @Schema(description = "그룹 ID (그룹 이벤트인 경우만)", example = "1")
    private Long groupId;

    @Schema(description = "후보 날짜 목록")
    @NotEmpty(message = "후보 날짜는 최소 1개 이상 필요합니다.")
    @Valid
    private List<CandidateDateWeb> dateList;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "후보 날짜 정보")
    public static class CandidateDateWeb {

        @Schema(description = "날짜 목록", example = "[\"2025-07-15\", \"2025-07-16\", \"2025-07-17\"]")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotEmpty(message = "날짜는 최소 1개 이상 필요합니다.")
        @Valid
        private List<@NotNull(message = "날짜는 필수입니다.") @FutureOrPresent(message = "과거 날짜는 선택할 수 없습니다.") LocalDate> dates;

        @Schema(description = "시작 시간", example = "09:00")
        @JsonFormat(pattern = "HH:mm")
        @NotNull(message = "시작 시간은 필수입니다.")
        private LocalTime startTime;

        @Schema(description = "종료 시간", example = "18:00")
        @JsonFormat(pattern = "HH:mm")
        @NotNull(message = "종료 시간은 필수입니다.")
        private LocalTime endTime;
    }
}