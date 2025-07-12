package com.grepp.spring.app.model.event.dto;

import com.grepp.spring.app.model.event.code.Role;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventMemberDto {
    private final Long eventId;
    private final String memberId;
    private final Role role;

    public static EventMemberDto toDto(Long eventId, String memberId, Role role) {
        return EventMemberDto.builder()
            .eventId(eventId)
            .memberId(memberId)
            .role(role)
            .build();
    }

    public static EventMember toEntity(EventMemberDto dto, Event event, Member member) {
        EventMember eventMember = new EventMember();
        eventMember.setEvent(event);
        eventMember.setMember(member);
        eventMember.setRole(dto.getRole());
        return eventMember;
    }
}
