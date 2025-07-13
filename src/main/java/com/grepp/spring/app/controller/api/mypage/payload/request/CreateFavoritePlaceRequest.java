package com.grepp.spring.app.controller.api.mypage.payload.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateFavoritePlaceRequest {
    @NotNull
    @Schema(example = ".강남역 8번 출구")
    private String stationName; // 역이름으로 수정
    @NotNull
    @Schema(example = "37.4979")
    private double latitude;
    @NotNull
    @Schema(example = "127.0276")
    private double longitude;



}
