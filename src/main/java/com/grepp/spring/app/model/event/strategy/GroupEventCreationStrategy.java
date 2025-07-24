package com.grepp.spring.app.model.event.strategy;

import com.grepp.spring.app.controller.api.event.payload.response.CreateEventResponse;
import com.grepp.spring.app.model.event.code.Role;
import com.grepp.spring.app.model.event.dto.CandidateDateDto;
import com.grepp.spring.app.model.event.dto.CreateEventDto;
import com.grepp.spring.app.model.event.dto.EventMemberDto;
import com.grepp.spring.app.model.event.entity.CandidateDate;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.repository.CandidateDateRepository;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.group.repository.GroupRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.infra.error.exceptions.event.EventNotFoundException;
import com.grepp.spring.infra.error.exceptions.event.NotEventMemberException;
import com.grepp.spring.infra.response.EventErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupEventCreationStrategy implements EventCreationStrategy {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final CandidateDateRepository candidateDateRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Event createEvent(CreateEventDto serviceRequest, String currentMemberId) {
        Group group = findAndValidateGroup(serviceRequest.getGroupId(), currentMemberId);

        Event event = createAndSaveEvent(serviceRequest, group);

        createEventMaster(event.getId(), currentMemberId);

        createCandidateDates(event, serviceRequest.getCandidateDates());

        return event;
    }

    private Group findAndValidateGroup(Long groupId, String currentMemberId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.GROUP_NOT_FOUND));

        GroupMember groupMember = groupMemberRepository.findByGroupIdAndMemberId(groupId, currentMemberId)
            .orElseThrow(() -> new NotEventMemberException(EventErrorCode.NOT_GROUP_MEMBER));

        return group;
    }

    private Event createAndSaveEvent(CreateEventDto serviceRequest, Group group) {
        Event event = CreateEventDto.toEntity(serviceRequest, group);

        return eventRepository.save(event);
    }

    private void createEventMaster(Long eventId, String memberId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.EVENT_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.MEMBER_NOT_FOUND));

        EventMemberDto masterDto = EventMemberDto.toDto(eventId, memberId, Role.ROLE_MASTER);
        EventMember eventMember = EventMemberDto.toEntity(masterDto, event, member);

        eventMemberRepository.save(eventMember);
    }

    private void createCandidateDates(Event event, List<CandidateDateDto> candidateDates) {
        List<CandidateDate> entities = CandidateDateDto.toEntityList(candidateDates, event);

        candidateDateRepository.saveAll(entities);
    }
}