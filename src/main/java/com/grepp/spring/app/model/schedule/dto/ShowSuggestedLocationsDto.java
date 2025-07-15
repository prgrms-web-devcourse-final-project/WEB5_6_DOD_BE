package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedules.payload.response.ShowSuggestedLocationsResponse;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShowSuggestedLocationsDto {
    private List<MetroInfoDto> suggestedLocations;
    private int noVoteCount;

    public static ShowSuggestedLocationsDto fromMetroInfoDto(List<MetroInfoDto> dto, int scheduleMemberNumber) {

        return ShowSuggestedLocationsDto.builder()
            .suggestedLocations(dto)
            .noVoteCount(dto.size())
            .build();
    }

    public static ShowSuggestedLocationsResponse fromDto(ShowSuggestedLocationsDto dto) {
        return ShowSuggestedLocationsResponse.builder()
            .suggestedLocations(dto.getSuggestedLocations())
            .noVoteCount(dto.getNoVoteCount())
            .build();
    }
}
