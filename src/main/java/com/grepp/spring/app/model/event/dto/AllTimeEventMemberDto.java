package com.grepp.spring.app.model.event.dto;

import lombok.Getter;

@Getter
public class AllTimeEventMemberDto {
    private final String memberId;

    public AllTimeEventMemberDto(String memberId) {
        this.memberId = memberId;
    }
}
