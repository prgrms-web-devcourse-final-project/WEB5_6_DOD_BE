package com.grepp.spring.app.controller.api.schedule.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WriteSuggestedLocationRequest {
    private String locationName;
    private Double latitude;
    private Double longitude;
}
