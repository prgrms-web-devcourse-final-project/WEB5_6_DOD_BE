package com.grepp.spring.app.controller.api.schedules.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDepartLocationRequest {
    private Long scheduleId;
    private String departLocationName;
    private Double longitude;
    private Double latitude;
}
