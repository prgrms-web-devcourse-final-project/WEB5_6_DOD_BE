package com.grepp.spring.app.controller.api.member.payload;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberInfoResponse {

    private String id;
    private String email;
    private String name;
    private Integer profileImageNumber;
    private String provider;
    private String role;

}
