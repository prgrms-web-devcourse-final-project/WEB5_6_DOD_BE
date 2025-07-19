package com.grepp.spring.app.controller.api.schedule.payload.response;

import com.grepp.spring.app.model.schedule.dto.MetroInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowVoteResultResponse {
    private MetroInfoDto suggestedLocation;

}
