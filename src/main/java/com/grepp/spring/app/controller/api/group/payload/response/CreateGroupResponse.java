package com.grepp.spring.app.controller.api.group.payload.response;

import com.grepp.spring.app.model.group.entity.Group;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateGroupResponse {
    private Long groupId;
    private String groupName;
    private String description;

    public CreateGroupResponse(Long groupId, String groupName, String description) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.description = description;
    }

    public static CreateGroupResponse createCreateGroupResponse(Group group){
        return new CreateGroupResponse(group.getId(), group.getName(), group.getDescription());
    }
}
