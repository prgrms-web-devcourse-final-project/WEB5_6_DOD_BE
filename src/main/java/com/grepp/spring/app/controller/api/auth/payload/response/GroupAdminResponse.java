package com.grepp.spring.app.controller.api.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupAdminResponse {

    private String groupId;
    private String groupName;

}
