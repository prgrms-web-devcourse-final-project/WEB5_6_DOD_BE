package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Vote;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VoteMiddleLocationDto {
    private ScheduleMember scheduleMemberId;
    private Location locationId;

    public static VoteMiddleLocationDto toDto(
        Optional<ScheduleMember> scheduleMemberId, Optional<Location> lId) {
        return VoteMiddleLocationDto.builder()
            .scheduleMemberId(scheduleMemberId.orElse(null))
            .locationId(lId.orElse(null))
            .build();
    }

    public static Vote fromDto(VoteMiddleLocationDto dto) {
        return Vote.builder()
            .scheduleMember(dto.getScheduleMemberId())
            .location(dto.getLocationId())
            .build();
    }
}
