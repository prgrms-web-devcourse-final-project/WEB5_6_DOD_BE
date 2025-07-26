package com.grepp.spring.app.model.schedule.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateScheduleMembersDto {
    private String memberId;
}
