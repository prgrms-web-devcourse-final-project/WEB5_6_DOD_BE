package com.grepp.spring.app.controller.api.group.payload.request;

import com.grepp.spring.app.model.group.code.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ControlGroupRoleRequest {
    private String userId;
    private GroupRole groupRole;

}
