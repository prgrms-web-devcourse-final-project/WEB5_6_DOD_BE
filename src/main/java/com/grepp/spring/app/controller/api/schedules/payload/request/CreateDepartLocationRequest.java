package com.grepp.spring.app.controller.api.schedules.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDepartLocationRequest {
    private String departLocationName;
    private Double longitude;   // 위도
    private Double latitude;    // 경도
}
