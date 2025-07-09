package com.grepp.spring.app.controller.api.group.payload.response;

import com.grepp.spring.app.model.group.dto.GroupUser;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShowGroupMemberResponse {
    private ArrayList<GroupUser> groupUser;
}
