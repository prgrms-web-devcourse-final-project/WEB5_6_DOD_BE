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
    private Double longitude;   // 위도
    private Double latitude;    // 경도

//
//    private Schedule scheduleId;
//    private String suggestedMemberId;
//    private VoteStatus status;


    public static CreateDepartLocationDto toDto(CreateDepartLocationRequest request) {
        return CreateDepartLocationDto.builder()
            .departLocationName(request.getDepartLocationName())
            .longitude(request.getLongitude())
            .latitude(request.getLatitude())
            .build();
    }

    public static CreateDepartLocationDto entityToDto(Metro metro) {
        return CreateDepartLocationDto.builder()
            .departLocationName(metro.getName())
            .longitude(metro.getLongitude())
            .latitude(metro.getLatitude())
            .build();
    }

}
