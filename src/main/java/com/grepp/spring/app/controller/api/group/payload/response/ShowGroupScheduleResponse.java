package com.grepp.spring.app.controller.api.group.payload.response;

import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.dto.ScheduleDetails;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShowGroupScheduleResponse {
    private Long groupId;
    private String groupName;
    private String groupDescription;
    private Long groupMemberNumbers;
    private GroupRole groupRole;
    private ArrayList<ScheduleDetails> scheduleDetails;

}
