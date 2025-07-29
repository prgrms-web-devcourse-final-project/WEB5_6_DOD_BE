package com.grepp.spring.app.model.event.service;

import com.grepp.spring.app.controller.api.event.payload.request.CreateEventRequest;
import com.grepp.spring.app.controller.api.event.payload.request.MyTimeScheduleRequest;
import com.grepp.spring.app.controller.api.event.payload.response.CreateEventResponse;
import com.grepp.spring.app.model.event.code.Role;
import com.grepp.spring.app.model.event.dto.CreateEventDto;
import com.grepp.spring.app.model.event.dto.EventMemberDto;
import com.grepp.spring.app.model.event.dto.JoinEventDto;
import com.grepp.spring.app.model.event.dto.MyTimeScheduleDto;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.entity.TempSchedule;
import com.grepp.spring.app.model.event.factory.EventCreationStrategyFactory;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.event.repository.TempScheduleRepository;
import com.grepp.spring.app.model.event.strategy.EventCreationStrategy;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.group.repository.GroupRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.infra.error.exceptions.event.*;
import com.grepp.spring.infra.response.EventErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventCommandService {

    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final TempScheduleRepository tempScheduleRepository;
    private final EventScheduleResultService eventScheduleResultService;
    private final EventCreationStrategyFactory strategyFactory;
    private final EventScheduleCacheService cacheService;

    public CreateEventResponse createEvent(CreateEventRequest webRequest, String currentMemberId) {
        CreateEventDto serviceRequest = CreateEventDto.toDto(webRequest, currentMemberId);

        EventCreationStrategy strategy = strategyFactory.getStrategy(serviceRequest.getGroupId());
        Event event = strategy.createEvent(serviceRequest, currentMemberId);

        CreateEventResponse response = new CreateEventResponse(
            event.getId(),
            event.getTitle(),
            event.getGroup().getId()
        );

        return response;
    }

    public void joinEvent(Long eventId, Long groupId, String currentMemberId) {
        JoinEventDto dto = JoinEventDto.toDto(eventId, currentMemberId);

        Event event = findEventOrThrow(dto.getEventId());
        Member member = findMemberOrThrow(dto.getMemberId());

        validateEventMemberIsAlreadyJoined(dto.getEventId(), dto.getMemberId());

        Long currentMemberCount = eventMemberRepository.countByEventId(dto.getEventId());
        event.validateCapacity(currentMemberCount);

        addUserToGroup(groupId, dto.getMemberId());

        EventMemberDto memberDto = EventMemberDto.toDto(dto.getEventId(), dto.getMemberId(), Role.ROLE_MEMBER);
        createEventMember(memberDto);

        cacheService.invalidateEventCache(eventId);
    }

    public void createOrUpdateMyTime(MyTimeScheduleRequest request, Long eventId, String currentMemberId) {
        MyTimeScheduleDto dto = MyTimeScheduleDto.toDto(request, eventId, currentMemberId);

        Event event = findEventOrThrow(dto.getEventId());
        EventMember eventMember = findEventMemberOrThrow(dto.getEventId(), dto.getMemberId());

        validateEventMemberIsAlreadyConfirmedSchedule(eventMember);

        for (MyTimeScheduleDto.DailyTimeSlotDto slot : dto.getDailyTimeSlots()) {
            updateOrCreateTempSchedule(eventMember, slot);
        }

        cacheService.invalidateEventCache(eventId);
    }

    private void updateOrCreateTempSchedule(EventMember eventMember, MyTimeScheduleDto.DailyTimeSlotDto slot) {
        LocalDate date = slot.getDate();

        Optional<TempSchedule> existingSchedule = tempScheduleRepository
            .findByEventMemberIdAndDateAndActivatedTrue(eventMember.getId(), date);

        if (existingSchedule.isPresent()) {
            TempSchedule schedule = existingSchedule.get();
            Long currentTimeBit = schedule.getTimeBit();
            Long newTimeBit = currentTimeBit ^ slot.getTimeBitAsLong();

            schedule.setTimeBit(newTimeBit);
            tempScheduleRepository.save(schedule);
        } else {
            TempSchedule newSchedule = MyTimeScheduleDto.DailyTimeSlotDto.toEntity(slot, eventMember);
            tempScheduleRepository.save(newSchedule);
        }
    }

    public void completeMyTime(Long eventId, String currentMemberId) {
        JoinEventDto dto = JoinEventDto.toDto(eventId, currentMemberId);

        Event event = findEventOrThrow(dto.getEventId());
        EventMember eventMember = findEventMemberOrThrow(dto.getEventId(), dto.getMemberId());

        List<TempSchedule> schedules = tempScheduleRepository
            .findAllByEventMemberIdAndActivatedTrue(eventMember.getId());

        if (schedules.isEmpty()) {
            throw new InvalidEventDataException(EventErrorCode.CANNOT_COMPLETE_EMPTY_SCHEDULE);
        }

        eventMember.confirmScheduleOrThrow();
        eventMemberRepository.save(eventMember);

        cacheService.invalidateEventCache(eventId);
    }

    public void createScheduleResult(Long eventId, String currentMemberId) {
        Event event = findEventOrThrow(eventId);
        EventMember eventMember = findEventMemberOrThrow(eventId, currentMemberId);
        eventScheduleResultService.createScheduleRecommendations(eventId);
    }

    private void createEventMember(EventMemberDto dto) {
        Event event = findEventOrThrow(dto.getEventId());
        Member member = findMemberOrThrow(dto.getMemberId());
        EventMember eventMember = EventMemberDto.toEntity(dto, event, member);
        eventMemberRepository.save(eventMember);
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

    private Event findEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.EVENT_NOT_FOUND));
    }

    private Member findMemberOrThrow(String memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.MEMBER_NOT_FOUND));
    }

    private EventMember findEventMemberOrThrow(Long eventId, String memberId) {
        return eventMemberRepository
            .findByEventIdAndMemberIdAndActivatedTrue(eventId, memberId)
            .orElseThrow(() -> new NotEventMemberException(EventErrorCode.NOT_EVENT_MEMBER));
    }

    private void validateEventMemberIsAlreadyJoined(Long eventId, String memberId) {
        if (eventMemberRepository.existsByEventIdAndMemberId(eventId, memberId)) {
            throw new AlreadyJoinedEventException(EventErrorCode.ALREADY_JOINED_EVENT);
        }
    }

    private static void validateEventMemberIsAlreadyConfirmedSchedule(EventMember eventMember) {
        if (eventMember.getConfirmed()) {
            throw new AlreadyConfirmedScheduleException(EventErrorCode.ALREADY_CONFIRMED_SCHEDULE);
        }
    }

}
