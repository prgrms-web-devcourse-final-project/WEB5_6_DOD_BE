package com.grepp.spring.app.controller.api.member.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModifyMemberInfoResponse {

    private String id;
    private String name;
    private Long profileImageNumber;

}
