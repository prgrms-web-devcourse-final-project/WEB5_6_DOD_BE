package com.grepp.spring.app.model.group.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GroupSchedule {
    private String location;
    private Long count;

}
