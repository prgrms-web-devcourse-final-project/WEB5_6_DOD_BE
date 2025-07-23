package com.grepp.spring.app.controller.api.group.payload.response;

import com.grepp.spring.app.model.group.entity.Group;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ModifyGroupInfoResponse {
    private Long groupId;
    private String groupName;
    private String description;

    public ModifyGroupInfoResponse(Long groupId, String groupName, String description) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.description = description;
    }

    public static ModifyGroupInfoResponse createModifyGroupInfoResponse(Group group) {
        return new ModifyGroupInfoResponse(group.getId(), group.getName(), group.getDescription());
    }
}
