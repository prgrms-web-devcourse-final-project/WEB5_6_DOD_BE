package com.grepp.spring.app.controller.api.group.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModifyGroupInfoRequest {
    private String groupName;
    private String description;

}
