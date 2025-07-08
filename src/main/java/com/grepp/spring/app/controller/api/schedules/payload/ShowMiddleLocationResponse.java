package com.grepp.spring.app.controller.api.schedules.payload;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShowMiddleLocationResponse {
    private String locationName;
    private Double latitude;
    private	Double longitude;
    private	Long voteCount;

    private List<String> metroLines; // 추가
    private List<String> stationColors; // 추가
}
