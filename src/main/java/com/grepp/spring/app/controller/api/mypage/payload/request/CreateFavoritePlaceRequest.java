package com.grepp.spring.app.controller.api.mypage.payload.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateFavoritePlaceRequest {
    @Schema(example = "강남역 2번 출구")
    @NotNull
    private String stationName; // 역이름으로 수정
    @Schema(example = "37.4979")
    @NotNull
    private double latitude;
    @Schema(example = "127.0276")
    @NotNull
    private double longitude;



}
