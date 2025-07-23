package com.grepp.spring.app.controller.api.group.payload.response;

import com.grepp.spring.app.model.member.entity.Member;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InviteGroupMemberResponse {
    private String memberId;
    private String memberName;
    private Long groupId;

    public InviteGroupMemberResponse(String memberId, String memberName, Long groupId) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.groupId = groupId;
    }

    public static InviteGroupMemberResponse createInviteGroupMemberResponse(Member member,
        Long groupId){
        return new InviteGroupMemberResponse(member.getId(), member.getName(), groupId);
    }

}
