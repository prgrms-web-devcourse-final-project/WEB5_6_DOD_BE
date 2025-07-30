package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Vote;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VoteMiddleLocationDto {
//    private ScheduleMember scheduleMember;
    private Long scheduleMemberId;
    private Location location;
    private Schedule schedule;

    public static VoteMiddleLocationDto toDto(Long scheduleMemberId, Location location, Schedule schedule) {
        return VoteMiddleLocationDto.builder()
            .scheduleMemberId(scheduleMemberId)
            .location(location)
            .schedule(schedule)
            .build();
    }

    public static Vote fromDto(VoteMiddleLocationDto dto,
        ScheduleMemberRepository scheduleMemberRepository) {

        ScheduleMember scheduleMember = scheduleMemberRepository.findById(dto.getScheduleMemberId())
            .orElseThrow(() -> new IllegalArgumentException("ScheduleMember not found with ID: " + dto.getScheduleMemberId()));
        return Vote.builder()
//            .scheduleMember(dto.getScheduleMember())
            .scheduleMember(scheduleMember)
            .location(dto.getLocation())
            .schedule(dto.getSchedule())
            .build();
    }
}
