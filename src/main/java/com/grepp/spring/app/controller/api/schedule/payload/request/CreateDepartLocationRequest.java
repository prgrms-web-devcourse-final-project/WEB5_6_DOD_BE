package com.grepp.spring.app.controller.api.schedule.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDepartLocationRequest {
    private String memberId; // 임시

    private String departLocationName; // 출발장소 명
    private Double longitude;   // 위도
    private Double latitude;    // 경도
}
