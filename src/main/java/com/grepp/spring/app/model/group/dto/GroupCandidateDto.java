package com.grepp.spring.app.model.group.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupCandidateDto {

    private Long groupId;
    private String groupName;
    private String description;

}
