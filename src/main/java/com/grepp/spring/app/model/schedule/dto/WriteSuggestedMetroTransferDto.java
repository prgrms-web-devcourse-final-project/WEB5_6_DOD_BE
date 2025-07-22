package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.model.schedule.entity.Line;
import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.MetroTransfer;
import com.grepp.spring.app.model.schedule.entity.Schedule;
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
public class WriteSuggestedMetroTransferDto {
    private Schedule schedule;
    private Location location;
    private String lineName;
    private String color;
    private Boolean isMemberSuggested;

    public static WriteSuggestedMetroTransferDto toDto (Schedule schedule, Location location, Line line) {
        return WriteSuggestedMetroTransferDto.builder()
            .schedule(schedule)
            .location(location)
            .lineName(line.getLineName())
            .color(line.getColor())
            .isMemberSuggested(true)
            .build();

    }

    public static MetroTransfer fromDto (WriteSuggestedMetroTransferDto dto) {
        return MetroTransfer.builder()
            .schedule(dto.getSchedule())
            .location(dto.getLocation())
            .lineName(dto.getLineName())
            .color(dto.getColor())
            .isMemberSuggested(dto.getIsMemberSuggested())
            .build();
    }
}
