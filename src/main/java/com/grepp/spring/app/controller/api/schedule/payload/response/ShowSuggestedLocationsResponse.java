package com.grepp.spring.app.controller.api.schedule.payload.response;

import com.grepp.spring.app.model.schedule.dto.MetroInfoDto;
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
public class ShowSuggestedLocationsResponse {

    private List<MetroInfoDto> suggestedLocations;
    private int noVoteCount;
    private int noDepartLocationCount;
}
