package com.grepp.spring.app.model.event.service;

import com.grepp.spring.app.controller.api.event.payload.request.CreateEventRequest;
import com.grepp.spring.app.model.event.code.Role;
import com.grepp.spring.app.model.event.dto.CandidateDateDto;
import com.grepp.spring.app.model.event.dto.CreateEventDto;
import com.grepp.spring.app.model.event.entity.CandidateDate;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.repository.*;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.member.entity.Member;
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
    // TODO: Member 패키지의 MemberRepository에 findById 메서드가 추가될 시, 주석 해제
//    private final MemberRepository memberRepository;
    // TODO: 추후 Event 패키지의 GroupRepository로 변경해야 함. 현재는 Event 패키지의 GroupRepository임
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
        // TODO: Member 패키지의 MemberRepository에 findById 메서드가 추가될 시, 주석 해제
//        createMasterMember(event, serviceRequest.getCurrentMemberId());
        createCandidateDates(event, serviceRequest.getCandidateDates());
    }

    // TODO: Member 패키지의 MemberRepository에 findById 메서드가 추가될 시, 주석 해제
//    private void createMasterMember(Event event, String memberId) {
//        Member member = memberRepository.findById(memberId)
//            .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다. ID: " + memberId));
//
//        EventMember eventMember = new EventMember();
//        eventMember.setEvent(event);
//        eventMember.setMember(member);
//        eventMember.setRole(Role.ROLE_MASTER);
//
//        eventMemberRepository.save(eventMember);
//        log.debug("마스터 멤버 생성 완료 - 회원ID: {}", memberId);
//    }

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

}