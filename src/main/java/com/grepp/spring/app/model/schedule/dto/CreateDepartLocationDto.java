package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedules.payload.request.CreateDepartLocationRequest;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateDepartLocationDto {
    private String departLocationName; // 출발장소 명
    private Double longitude;   // 위도
    private Double latitude;    // 경도

    public static CreateDepartLocationDto toDto(CreateDepartLocationRequest request) {
        return CreateDepartLocationDto.builder()
            .departLocationName(request.getDepartLocationName())
            .longitude(request.getLongitude())
            .latitude(request.getLatitude())
            .build();
    }

}
