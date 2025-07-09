package com.grepp.spring.app.controller.api.auth.payload.request;

import com.grepp.spring.app.model.group.code.GroupRole;
import lombok.Data;

@Data
public class AccountDeactivateRequest {

    private GroupRole groupRole;

}
