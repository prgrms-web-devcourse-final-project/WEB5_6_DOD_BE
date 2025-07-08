package com.grepp.spring.app.controller.api.auth.payload;

import com.grepp.spring.app.controller.api.group.groupDto.groupRole.GroupRole;
import lombok.Data;

@Data
public class AccountDeactivateRequest {

    private GroupRole groupRole;

}
