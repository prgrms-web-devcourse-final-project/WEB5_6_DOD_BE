package com.grepp.spring.app.controller.api.group.payload.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateGroupResponse {
    private Long groupId;
    private String groupName;
    private String description;

}
