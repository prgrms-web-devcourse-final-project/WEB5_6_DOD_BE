package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedule.payload.request.WriteSuggestedLocationRequest;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.schedule.code.VoteStatus;
import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.Metro;
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
public class WriteSuggestedLocationDto {
    private String locationName;
    private Double latitude;
    private Double longitude;
    private VoteStatus status;
    private Schedule schedule;
    private String memberId;
    private int voteCount;


public static WriteSuggestedLocationDto requestToDto(WriteSuggestedLocationRequest request, Schedule schedule, Member member) {
        return WriteSuggestedLocationDto.builder()
            .locationName(request.getLocationName())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .status(VoteStatus.DEFAULT)
            .schedule(schedule)
            .memberId(member.getId())
            .voteCount(0)
            .build();
    }

    public static Location fromDto(WriteSuggestedLocationDto dto) {
        return Location.builder()
            .latitude(dto.getLatitude())
            .longitude(dto.getLongitude())
            .name(dto.getLocationName())
            .status(dto.getStatus())
            .schedule(dto.schedule)
            .suggestedMemberId(dto.memberId)
            .voteCount(dto.getVoteCount())
            .build();
    }

    public static Location metroToEntity(Metro metro, Schedule schedule, Member member) {
        return Location.builder()
            .latitude(metro.getLatitude())
            .longitude(metro.getLongitude())
            .name(metro.getName())
            .status(VoteStatus.DEFAULT)
            .schedule(schedule)
            .suggestedMemberId(member.getId())
            .voteCount(0)
            .build();
    }
}
