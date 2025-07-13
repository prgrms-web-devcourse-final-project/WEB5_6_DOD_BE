package com.grepp.spring.app.controller.api.mypage.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateFavoriteTimeRequest {

    @Pattern(regexp = "^[0-9A-Fa-f]{12}$", message = "타임비트는 12자리 16진수여야 합니다")
    @Schema(description = "월요일 타임비트 (12자리 16진수)")
    private String timeBitMon;

    @Pattern(regexp = "^[0-9A-Fa-f]{12}$", message = "타임비트는 12자리 16진수여야 합니다")
    @Schema(description = "화요일 타임비트 (12자리 16진수)")
    private String timeBitTue;

    @Pattern(regexp = "^[0-9A-Fa-f]{12}$", message = "타임비트는 12자리 16진수여야 합니다")
    @Schema(description = "수요일 타임비트 (12자리 16진수)")
    private String timeBitWed;

    @Pattern(regexp = "^[0-9A-Fa-f]{12}$", message = "타임비트는 12자리 16진수여야 합니다")
    @Schema(description = "목요일 타임비트 (12자리 16진수)")
    private String timeBitThu;

    @Pattern(regexp = "^[0-9A-Fa-f]{12}$", message = "타임비트는 12자리 16진수여야 합니다")
    @Schema(description = "금요일 타임비트 (12자리 16진수)")
    private String timeBitFri;

    @Pattern(regexp = "^[0-9A-Fa-f]{12}$", message = "타임비트는 12자리 16진수여야 합니다")
    @Schema(description = "토요일 타임비트 (12자리 16진수)")
    private String timeBitSat;

    @Pattern(regexp = "^[0-9A-Fa-f]{12}$", message = "타임비트는 12자리 16진수여야 합니다")
    @Schema(description = "일요일 타임비트 (12자리 16진수)")
    private String timeBitSun;

}
