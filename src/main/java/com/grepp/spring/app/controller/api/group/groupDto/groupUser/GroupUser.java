package com.grepp.spring.app.controller.api.group.groupDto.groupUser;

import com.grepp.spring.app.controller.api.group.groupDto.groupRole.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GroupUser {
    private String userId;
    private String userName;
    private GroupRole groupRole;
}
