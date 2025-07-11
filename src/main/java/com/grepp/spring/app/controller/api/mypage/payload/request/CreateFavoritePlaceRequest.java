package com.grepp.spring.app.controller.api.mypage.payload.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateFavoritePlaceRequest {
    @NotNull
    private String stationName; // 역이름으로 수정
    @NotNull
    private double latitude;
    @NotNull
    private double longitude;



}
