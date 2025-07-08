package com.grepp.spring.app.controller.api.group.payload;

import java.util.ArrayList;
import lombok.Getter;

@Getter
public class InviteGroupMemberRequest {
    private ArrayList<String> userIds;

}
