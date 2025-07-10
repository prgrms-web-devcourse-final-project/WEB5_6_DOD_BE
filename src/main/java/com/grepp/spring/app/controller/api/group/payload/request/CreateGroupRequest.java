package com.grepp.spring.app.controller.api.group.payload.request;

import lombok.Getter;

@Getter
public class CreateGroupRequest {
    private String groupName;
    private String description;

}
