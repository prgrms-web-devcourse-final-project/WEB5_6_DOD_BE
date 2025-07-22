package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedule.payload.response.ShowVoteMembersResponse;
import com.grepp.spring.app.model.member.entity.Member;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteMemberDto {
    private String memberId;

    public static VoteMemberDto toDto(Member member) {
        return VoteMemberDto.builder()
            .memberId(member.getId())
            .build();
    }

    public static ShowVoteMembersResponse fromDto(List<VoteMemberDto> dto) {

        return ShowVoteMembersResponse.builder()
            .voteMembersList(dto)
            .build();
    }
}
