package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.model.schedule.code.ScheduleRole;
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
public class ScheduleMembersDto {
    private String id;
    private Long scheduleMemberId;
    private String name;
    private ScheduleRole scheduleRole;
}
