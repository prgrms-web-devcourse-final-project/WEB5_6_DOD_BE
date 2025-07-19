package com.grepp.spring.app.controller.api.schedule.payload.response;

import com.grepp.spring.app.model.schedule.dto.MetroLineDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShowMiddleLocationResponse {
    private String locationName;
    private Double latitude;
    private	Double longitude;
//    private	Long voteCount;

//    private List<String> metroLines; // 추가
//    private List<String> stationColors; // 추가

    private List<MetroLineDto> metroLines;  // (호선, 호선 색)

}
