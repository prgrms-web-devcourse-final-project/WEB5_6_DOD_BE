package com.grepp.spring.app.model.group.dto;

import com.grepp.spring.app.model.group.code.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GroupUser {
    private String userId;
    private String userName;
    private GroupRole groupRole;
}
