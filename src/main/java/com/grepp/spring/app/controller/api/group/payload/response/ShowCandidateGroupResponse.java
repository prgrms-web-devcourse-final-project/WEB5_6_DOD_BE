package com.grepp.spring.app.controller.api.group.payload.response;


import com.grepp.spring.app.model.group.dto.GroupCandidateDto;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShowCandidateGroupResponse {
    private ArrayList<GroupCandidateDto> candidateGroups;

}
