package com.grepp.spring.app.controller.api.group.payload.response;

import com.grepp.spring.app.model.group.dto.GroupDetail;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShowGroupResponse {
    private ArrayList<GroupDetail> groupDetails;
}
