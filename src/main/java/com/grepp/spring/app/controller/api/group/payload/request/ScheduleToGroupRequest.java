package com.grepp.spring.app.controller.api.group.payload.request;

import lombok.Getter;

@Getter
public class ScheduleToGroupRequest {
    private Long groupId;
    private Long scheduleId;

}
