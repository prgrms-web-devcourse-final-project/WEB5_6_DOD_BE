package com.grepp.spring.app.controller.api.schedules.payload;

import com.grepp.spring.app.model.member.domain.Role;
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
