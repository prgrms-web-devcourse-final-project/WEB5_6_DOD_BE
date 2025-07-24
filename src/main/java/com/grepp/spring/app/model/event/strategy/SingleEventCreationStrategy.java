package com.grepp.spring.app.model.event.strategy;

import com.grepp.spring.app.controller.api.event.payload.response.CreateEventResponse;
import com.grepp.spring.app.model.event.code.Role;
import com.grepp.spring.app.model.event.dto.CandidateDateDto;
import com.grepp.spring.app.model.event.dto.CreateEventDto;
import com.grepp.spring.app.model.event.dto.EventMemberDto;
import com.grepp.spring.app.model.event.dto.TempGroupCreateDto;
import com.grepp.spring.app.model.event.entity.CandidateDate;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.repository.CandidateDateRepository;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.group.repository.GroupRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.infra.error.exceptions.event.EventNotFoundException;
import com.grepp.spring.infra.error.exceptions.event.InvalidEventDataException;
import com.grepp.spring.infra.response.EventErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SingleEventCreationStrategy implements EventCreationStrategy {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final CandidateDateRepository candidateDateRepository;

    @Override
    @Transactional
    public Event createEvent(CreateEventDto serviceRequest, String currentMemberId) {
        Group tempGroup = createTempGroupForSingleEvent(serviceRequest.getTitle(), serviceRequest.getDescription());

        Event event = createAndSaveEvent(serviceRequest, tempGroup);

        addUserToGroup(tempGroup.getId(), currentMemberId);

        createEventMaster(event.getId(), currentMemberId);

        createCandidateDates(event, serviceRequest.getCandidateDates());

        return event;
    }

    private Group createTempGroupForSingleEvent(String eventTitle, String eventDescription) {
        TempGroupCreateDto tempGroupDto = TempGroupCreateDto.forSingleEvent(eventTitle, eventDescription);
        Group tempGroup = TempGroupCreateDto.toEntity(tempGroupDto);

        return groupRepository.save(tempGroup);
    }

    private Event createAndSaveEvent(CreateEventDto serviceRequest, Group tempGroup) {
        Event event = CreateEventDto.toEntity(serviceRequest, tempGroup);

        return eventRepository.save(event);
    }

    // TODO: 그룹 멤버 추가 로직 개선 필요
    private void addUserToGroup(Long groupId, String memberId) {
        try {
            Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EventNotFoundException(EventErrorCode.GROUP_NOT_FOUND));

            Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EventNotFoundException(EventErrorCode.MEMBER_NOT_FOUND));

            Optional<GroupMember> existingGroupMember = groupMemberRepository
                .findByGroupIdAndMemberId(groupId, memberId);

            if (existingGroupMember.isPresent()) {
                return;
            }

            GroupMember groupMember = new GroupMember();
            groupMember.setGroup(group);
            groupMember.setMember(member);
            groupMember.setRole(GroupRole.GROUP_MEMBER);
            groupMember.setGroupAdmin(false);

            groupMemberRepository.save(groupMember);
        } catch (EventNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidEventDataException(EventErrorCode.INVALID_EVENT_DATA);
        }
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