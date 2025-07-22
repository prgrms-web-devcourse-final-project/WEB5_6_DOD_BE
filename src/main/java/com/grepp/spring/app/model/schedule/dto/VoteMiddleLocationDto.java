package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Vote;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VoteMiddleLocationDto {
    private ScheduleMember scheduleMember;
    private Location location;
    private Schedule schedule;

    public static VoteMiddleLocationDto toDto(ScheduleMember scheduleMember, Location location, Schedule schedule) {
        return VoteMiddleLocationDto.builder()
            .scheduleMember(scheduleMember)
            .location(location)
            .schedule(schedule)
            .build();
    }

    public static Vote fromDto(VoteMiddleLocationDto dto) {
        return Vote.builder()
            .scheduleMember(dto.getScheduleMember())
            .location(dto.getLocation())
            .schedule(dto.getSchedule())
            .build();
    }
}
