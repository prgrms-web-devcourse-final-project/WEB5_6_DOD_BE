package com.grepp.spring.app.model.event.dto;

import com.grepp.spring.app.model.group.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TempGroupCreateDto {

    private String name;
    private String description;

    public static TempGroupCreateDto forSingleEvent(String eventTitle, String eventDescription) {
        return new TempGroupCreateDto(eventTitle, eventDescription);
    }

    public static Group toEntity(TempGroupCreateDto dto) {
        Group tempGroup = new Group();
        tempGroup.setName(dto.getName());
        tempGroup.setDescription(dto.getDescription());
        tempGroup.setIsGrouped(false);
        return tempGroup;
    }
}