package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedule.payload.response.ShowSuggestedLocationsResponse;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShowSuggestedLocationsDto {
    private List<MetroInfoDto> suggestedLocations;
    private int noVoteCount; // 투표하지 않은 사람의 수
    private int noDepartLocationCount; // 추가 // 출발장소를 등록하지 않은 사람의 수


    public static ShowSuggestedLocationsDto fromMetroInfoDto(List<MetroInfoDto> dto, int scheduleMemberNumber, int voteCount, int departLocationCount) {

        return ShowSuggestedLocationsDto.builder()
            .suggestedLocations(dto)
            .noVoteCount(scheduleMemberNumber - voteCount)
            .noDepartLocationCount(scheduleMemberNumber - departLocationCount)
            .build();
    }

    public static ShowSuggestedLocationsResponse fromDto(ShowSuggestedLocationsDto dto) {
        return ShowSuggestedLocationsResponse.builder()
            .suggestedLocations(dto.getSuggestedLocations())
            .noVoteCount(dto.getNoVoteCount())
            .noDepartLocationCount(dto.getNoDepartLocationCount())
            .build();
    }
}
