package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.Schedule;
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
    private Schedule scheduleId;

    public static VoteMiddleLocationDto toDto(
        Optional<ScheduleMember> scheduleMemberId, Optional<Location> lId, Schedule sId) {
        return VoteMiddleLocationDto.builder()
            .scheduleMemberId(scheduleMemberId.orElse(null))
            .locationId(lId.orElse(null))
            .scheduleId(sId)
            .build();
    }

    public static Vote fromDto(VoteMiddleLocationDto dto) {
        return Vote.builder()
            .scheduleMember(dto.getScheduleMemberId())
            .location(dto.getLocationId())
            .schedule(dto.getScheduleId())
            .build();
    }
}
