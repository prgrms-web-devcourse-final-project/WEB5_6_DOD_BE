package com.grepp.spring.app.model.group.dto;

import java.time.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeekDetail {
    private DayOfWeek weekDay;
    private Long count;
}
