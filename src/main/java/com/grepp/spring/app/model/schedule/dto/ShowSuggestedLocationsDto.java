package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedules.payload.response.ShowSuggestedLocationsResponse;
import com.grepp.spring.app.model.schedule.entity.Location;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShowSuggestedLocationsDto {
    private List<MetroInfoDto> suggestedLocations;
    private int noVoteCount;

    public static ShowSuggestedLocationsDto from(List<MetroInfoDto> dto) {

        return ShowSuggestedLocationsDto.builder()
            .suggestedLocations(dto)
            .build();
    }

    public static ShowSuggestedLocationsResponse fromDto(ShowSuggestedLocationsDto dto) {
        return ShowSuggestedLocationsResponse.builder()
            .suggestedLocations(dto.getSuggestedLocations())
            .noVoteCount(dto.getNoVoteCount())
            .build();
    }
}
