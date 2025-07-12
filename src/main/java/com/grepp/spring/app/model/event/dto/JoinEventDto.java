package com.grepp.spring.app.model.event.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinEventDto {

    private final Long eventId;
    private final String memberId;

    public static JoinEventDto toDto(Long eventId, String memberId) {
        return JoinEventDto.builder()
            .eventId(eventId)
            .memberId(memberId)
            .build();
    }

}