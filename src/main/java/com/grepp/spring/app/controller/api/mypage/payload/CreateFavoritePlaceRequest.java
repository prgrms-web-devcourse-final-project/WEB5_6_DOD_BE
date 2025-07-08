package com.grepp.spring.app.controller.api.mypage.payload;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateFavoritePlaceRequest {

    private String stationName; // 역이름으로 수정
    private double latitude;
    private double longitude;



}
