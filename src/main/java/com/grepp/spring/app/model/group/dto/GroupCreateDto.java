package com.grepp.spring.app.model.group.dto;

import com.grepp.spring.app.controller.api.group.payload.request.CreateGroupRequest;
import com.grepp.spring.app.model.group.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupCreateDto {

    private String name;
    private String description;

    // request로 GroupCreateDto 생성하는 생성자
    public GroupCreateDto(CreateGroupRequest request){
        this.name = request.getGroupName();
        this.description = request.getDescription();
    }

    // request to Dto
    public static GroupCreateDto toDto(CreateGroupRequest request){
        return new GroupCreateDto(request);
    }

    // Dto to Entity
    public static Group toEntity(GroupCreateDto groupCreateDto){
        Group group = new Group();
        group.setName(groupCreateDto.getName());
        group.setDescription(groupCreateDto.getDescription());
        group.setIsGrouped(true);
        return group;
    }


}
