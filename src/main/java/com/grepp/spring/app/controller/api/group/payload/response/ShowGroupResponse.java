package com.grepp.spring.app.controller.api.group.payload.response;

import com.grepp.spring.app.model.group.dto.GroupDetailDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ShowGroupResponse {
    private List<GroupDetailDto> groupDetails;
}
