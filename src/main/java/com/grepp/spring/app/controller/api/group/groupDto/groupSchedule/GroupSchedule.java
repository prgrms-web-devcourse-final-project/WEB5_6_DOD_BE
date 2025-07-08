package com.grepp.spring.app.controller.api.group.groupDto.groupSchedule;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GroupSchedule {
    private Long scheduleId;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


}
