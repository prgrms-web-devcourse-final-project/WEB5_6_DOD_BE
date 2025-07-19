package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedule.payload.request.CreateDepartLocationRequest;
import com.grepp.spring.app.model.schedule.entity.Metro;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateDepartLocationDto {
    private String departLocationName; // 출발장소 명
    private Double latitude;    // 위도
    private Double longitude;   // 경도

//
//    private Schedule scheduleId;
//    private String suggestedMemberId;
//    private VoteStatus status;


    public static CreateDepartLocationDto toDto(CreateDepartLocationRequest request) {
        return CreateDepartLocationDto.builder()
            .departLocationName(request.getDepartLocationName())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .build();
    }

    public static CreateDepartLocationDto entityToDto(Metro metro) {
        return CreateDepartLocationDto.builder()
            .departLocationName(metro.getName())
            .latitude(metro.getLatitude())
            .longitude(metro.getLongitude())
            .build();
    }

}
