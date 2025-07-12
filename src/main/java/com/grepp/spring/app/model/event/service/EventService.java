package com.grepp.spring.app.model.event.service;

import com.grepp.spring.app.controller.api.event.payload.request.CreateEventRequest;
import com.grepp.spring.app.model.event.code.Role;
import com.grepp.spring.app.model.event.dto.CandidateDateDto;
import com.grepp.spring.app.model.event.dto.CreateEventDto;
import com.grepp.spring.app.model.event.dto.EventMemberDto;
import com.grepp.spring.app.model.event.dto.JoinEventDto;
import com.grepp.spring.app.model.event.entity.CandidateDate;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.repository.*;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.group.repository.GroupRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final CandidateDateRepository candidateDateRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void createEvent(CreateEventRequest webRequest, String currentMemberId) {
        CreateEventDto serviceRequest = CreateEventDto.toDto(webRequest, currentMemberId);

        validate(serviceRequest);

        Event event = null;

        if (serviceRequest.getGroupId() != null) {
            Group group = groupRepository.findById(serviceRequest.getGroupId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 그룹입니다. ID: " + serviceRequest.getGroupId()));

            GroupMember groupMember = groupMemberRepository.findByGroupIdAndMemberId(serviceRequest.getGroupId(), currentMemberId)
                .orElseThrow(() -> new NotFoundException("그룹에 속하지 않은 회원입니다. 그룹ID: " + serviceRequest.getGroupId()));

            event = CreateEventDto.toEntity(serviceRequest, group);
        } else {
            event = CreateEventDto.toEntity(serviceRequest);
        }

        event = eventRepository.save(event);
        EventMemberDto masterDto = EventMemberDto.toDto(event.getId(), currentMemberId, Role.ROLE_MASTER);
        createEventMember(masterDto);
        createCandidateDates(event, serviceRequest.getCandidateDates());
    }

    private void createCandidateDates(Event event, List<CandidateDateDto> candidateDates) {
        List<CandidateDate> entities = CandidateDateDto.toEntityList(candidateDates, event);
        candidateDateRepository.saveAll(entities);
        log.debug("후보 날짜 생성 완료 - 개수: {}", entities.size());
    }

    private void validate(CreateEventDto serviceRequest) {
        if (!serviceRequest.isValid()) {
            throw new IllegalArgumentException("유효하지 않은 이벤트 생성 요청입니다.");
        }

        validateCandidateDates(serviceRequest.getCandidateDates());

        // TODO: 비즈니스 규칙 추가
        // 이벤트 제한 검증 (예: 최대 인원, 그룹 권한 등)
    }

    private void validateCandidateDates(List<CandidateDateDto> candidateDates) {
        if (candidateDates == null || candidateDates.isEmpty()) {
            throw new IllegalArgumentException("후보 날짜는 최소 1개 이상 필요합니다.");
        }
        // TODO: 추가 검증 로직 구현
    }

    @Transactional
    public void joinEvent(Long eventId, String currentMemberId) {

        JoinEventDto dto = JoinEventDto.toDto(eventId, currentMemberId);

        Event event = eventRepository.findById(dto.getEventId())
            .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));

        Member member = memberRepository.findById(dto.getMemberId())
            .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        if (eventMemberRepository.existsByEventIdAndMemberId(dto.getEventId(), dto.getMemberId())) {
            throw new IllegalStateException("이미 참여 중인 이벤트입니다.");
        }

        Long currentMemberCount = eventMemberRepository.countByEventId(dto.getEventId());
        validateEventCapacity(event, currentMemberCount);

        if (event.getGroup() != null) {
            validateGroupMembership(event.getGroup().getId(), dto.getMemberId());
        }

        EventMemberDto memberDto = EventMemberDto.toDto(dto.getEventId(), dto.getMemberId(), Role.ROLE_MEMBER);
        createEventMember(memberDto);

    }

    private void validateEventCapacity(Event event, Long currentMemberCount) {
        if (event.getMaxMember() != null && currentMemberCount >= event.getMaxMember()) {
            throw new IllegalStateException("이벤트 정원이 초과되었습니다.");
        }
    }

    private void validateGroupMembership(Long groupId, String memberId) {
        boolean isMember = groupMemberRepository.findByGroupIdAndMemberId(groupId, memberId).isPresent();
        if (!isMember) {
            throw new IllegalStateException("그룹 멤버만 참여할 수 있는 이벤트입니다.");
        }
    }

    @Transactional
    public void createEventMember(EventMemberDto dto) {
        Event event = eventRepository.findById(dto.getEventId())
            .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다. ID: " + dto.getEventId()));

        Member member = memberRepository.findById(dto.getMemberId())
            .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다. ID: " + dto.getMemberId()));

        EventMember eventMember = EventMemberDto.toEntity(dto, event, member);
        eventMemberRepository.save(eventMember);
    }

}