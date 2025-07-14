package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedules.payload.response.ShowSuggestedLocationsResponse;
import com.grepp.spring.app.model.schedule.entity.Location;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetroInfoDto {
    private String locationName;
    private Double latitude;
    private Double longitude;
    private List<MetroTransferDto> metroTransfer;

    public static List<MetroInfoDto> toDto(Location location, List<MetroTransferDto> dto) {

        MetroInfoDto infoDto =  MetroInfoDto.builder()
            .locationName(location.getName())
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .metroTransfer(dto)
            .build();

        return List.of(infoDto);

    }

}
