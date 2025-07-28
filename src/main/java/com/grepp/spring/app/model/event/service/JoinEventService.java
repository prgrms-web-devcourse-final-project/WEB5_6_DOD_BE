package com.grepp.spring.app.model.event.service;

import com.grepp.spring.app.model.event.code.Role;
import com.grepp.spring.app.model.event.dto.EventMemberDto;
import com.grepp.spring.app.model.event.dto.JoinEventDto;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.group.repository.GroupRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.infra.error.exceptions.event.AlreadyJoinedEventException;
import com.grepp.spring.infra.error.exceptions.event.EventNotFoundException;
import com.grepp.spring.infra.error.exceptions.event.InvalidEventDataException;
import com.grepp.spring.infra.response.EventErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinEventService {


    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public void joinEvent(Long eventId, Long groupId, String currentMemberId) {
        JoinEventDto dto = JoinEventDto.toDto(eventId, currentMemberId);

        Event event = findEventOrThrow(dto.getEventId());
        Member member = findMemberOrThrow(dto.getMemberId());

        validateEventMemberIsAlreadyJoined(dto.getEventId(), dto.getMemberId());

        Long currentMemberCount = eventMemberRepository.countByEventId(dto.getEventId());
        event.validateCapacity(currentMemberCount);

        addUserToGroup(groupId, dto.getMemberId());

        EventMemberDto memberDto = EventMemberDto.toDto(dto.getEventId(), dto.getMemberId(),
            Role.ROLE_MEMBER);
        createEventMember(memberDto);
    }

    private Event findEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.EVENT_NOT_FOUND));
    }

    private Member findMemberOrThrow(String memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateEventMemberIsAlreadyJoined(Long eventId, String memberId) {
        if (eventMemberRepository.existsByEventIdAndMemberId(eventId, memberId)) {
            throw new AlreadyJoinedEventException(EventErrorCode.ALREADY_JOINED_EVENT);
        }
    }

    private void addUserToGroup(Long groupId, String memberId) {
        try {
            Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EventNotFoundException(EventErrorCode.GROUP_NOT_FOUND));

            Member member = findMemberOrThrow(memberId);

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

    private void createEventMember(EventMemberDto dto) {
        Event event = findEventOrThrow(dto.getEventId());
        Member member = findMemberOrThrow(dto.getMemberId());
        EventMember eventMember = EventMemberDto.toEntity(dto, event, member);
        eventMemberRepository.save(eventMember);
    }

}
