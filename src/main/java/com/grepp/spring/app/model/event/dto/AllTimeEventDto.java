package com.grepp.spring.app.model.event.dto;

import lombok.Getter;

@Getter
public class AllTimeEventDto {
    private final Long eventId;
    private final String eventTitle;
    private final String eventDescription;
    private final Integer eventMaxMember;

    public AllTimeEventDto(Long eventId, String eventTitle, String eventDescription, Integer eventMaxMember) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventMaxMember = eventMaxMember;
    }

}
