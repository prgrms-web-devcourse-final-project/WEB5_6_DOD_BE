package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.model.schedule.entity.Line;
import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.Metro;
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
public class DepartLocationMetroTransferDto {

    private Schedule scheduleId;
    private Location locationId;
    private String lineName;
    private String color;

    public static DepartLocationMetroTransferDto toDto(Location location, Line line) {
        return DepartLocationMetroTransferDto.builder()
            .scheduleId(location.getSchedule())
            .locationId(location)
            .lineName(line.getLineName())
            .color(line.getColor())
            .build();
    }

    public static MetroTransfer fromDto(DepartLocationMetroTransferDto dto) {
        return MetroTransfer.builder()
            .schedule(dto.getScheduleId())
            .location(dto.getLocationId())
            .lineName(dto.getLineName())
            .color(dto.getColor())
            .build();
    }

}
