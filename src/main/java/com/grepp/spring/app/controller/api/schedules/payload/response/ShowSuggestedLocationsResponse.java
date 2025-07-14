package com.grepp.spring.app.controller.api.schedules.payload.response;

import com.grepp.spring.app.model.schedule.code.VoteStatus;
import com.grepp.spring.app.model.schedule.dto.MetroLineDto;
import com.grepp.spring.app.model.schedule.dto.MetroInfoDto;
import java.util.List;
import java.util.Map;
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
    private String locationName;
    private Double latitude;
    private	Double longitude;
    private	Long suggestedMemberId;
    private Long voteCount;
    private VoteStatus SCHEDULES_STATUS;

    private List<MetroLineDto> metroLines;  // (호선, 호선 색)


//    private List<String> metroLines; // 추가
//    private List<String> stationColors; // 추가
//    private int noVoteCount; // 추가. 투표하지 않은 사람 수

    private List<MetroInfoDto> suggestedLocations;
    private int noVoteCount;
}
