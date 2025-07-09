package com.grepp.spring.app.controller.api.group.payload;

import com.grepp.spring.app.controller.api.group.groupDto.groupDetail.GroupDetail;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShowGroupResponse {
    private ArrayList<GroupDetail> groupDetails;
}
