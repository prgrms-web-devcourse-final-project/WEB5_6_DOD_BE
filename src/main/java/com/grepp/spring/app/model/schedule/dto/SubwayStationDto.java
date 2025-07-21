package com.grepp.spring.app.model.schedule.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.grepp.spring.app.model.schedule.code.VoteStatus;
import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.Schedule;
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
public class SubwayStationDto {
    private Schedule schedule;
    private String name;
    private double longitude;
    private double latitude;
    private VoteStatus status;
    private int voteCount;

    public static SubwayStationDto toDto(JsonNode jsonNode, Schedule schedule) {
        return SubwayStationDto.builder()
            .schedule(schedule)
            .name(jsonNode.get("place_name").asText().split(" ")[0])
            .latitude(jsonNode.get("x").asDouble())
            .longitude(jsonNode.get("y").asDouble())
            .status(VoteStatus.DEFAULT)
            .voteCount(0)
            .build();
    }

    public static Location fromDto(SubwayStationDto subwayStationDto) {
        return Location.builder()
            .schedule(subwayStationDto.schedule)
            .name(subwayStationDto.getName())
            .latitude(subwayStationDto.getLatitude())
            .longitude(subwayStationDto.getLongitude())
            .status(subwayStationDto.getStatus())
            .voteCount(subwayStationDto.getVoteCount())
            .build();
    }
}
