package com.grepp.spring.app.controller.api.group.groupDto.groupDetail;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupDetail {
    private Long groupId;
    private String groupName;
    private String description;
    private Integer groupMemberNum;

}
