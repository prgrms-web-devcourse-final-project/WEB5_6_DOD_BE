package com.grepp.spring.app.controller.api.group.payload;

import lombok.Getter;

@Getter
public class ScheduleToGroupRequest {
    private Long groupId;
    private Long scheduleId;

}
