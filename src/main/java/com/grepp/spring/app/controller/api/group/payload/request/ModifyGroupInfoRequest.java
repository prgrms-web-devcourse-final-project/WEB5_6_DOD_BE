package com.grepp.spring.app.controller.api.group.payload.request;

import lombok.Data;

@Data
public class ModifyGroupInfoRequest {
    private String groupName;
    private String description;

}
