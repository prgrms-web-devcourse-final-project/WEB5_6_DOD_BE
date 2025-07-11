package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleMemberRolesDto {
    private String memberId;
    private ScheduleRole role;
}
