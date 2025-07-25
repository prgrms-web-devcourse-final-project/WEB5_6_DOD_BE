package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ScheduleMemberRolesDto {
    private String memberId;
    private ScheduleRole role;
}
