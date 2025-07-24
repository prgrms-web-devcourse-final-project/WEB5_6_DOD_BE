package com.grepp.spring.app.model.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinEventDto {

    private final Long eventId;
    private final String memberId;

    public static JoinEventDto toDto(Long eventId, String memberId) {
        return new JoinEventDto(eventId, memberId);
    }

}