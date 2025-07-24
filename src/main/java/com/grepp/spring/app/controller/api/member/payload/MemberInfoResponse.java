package com.grepp.spring.app.controller.api.member.payload;

import com.grepp.spring.app.model.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MemberInfoResponse {

    private String id;
    private String email;
    private String name;
    private Integer profileImageNumber;
    private String provider;
    private String role;

    // MemberInfoResponse 생성하는 정적 팩토리 메서드
    public static MemberInfoResponse from(Member member) {
        MemberInfoResponse response = new MemberInfoResponse();
        response.setId(member.getId());
        response.setEmail(member.getEmail());
        response.setName(member.getName());
        response.setProfileImageNumber(member.getProfileImageNumber());
        response.setProvider(member.getProvider().name());
        response.setRole(member.getRole().name());
        return response;
    }

}
