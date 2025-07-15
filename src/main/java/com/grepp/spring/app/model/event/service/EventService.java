package com.grepp.spring.app.model.event.service;

import com.grepp.spring.app.controller.api.event.payload.request.CreateEventRequest;
import com.grepp.spring.app.controller.api.event.payload.request.MyTimeScheduleRequest;
import com.grepp.spring.app.controller.api.event.payload.response.AllTimeScheduleResponse;
import com.grepp.spring.app.controller.api.event.payload.response.ScheduleResultResponse;
import com.grepp.spring.app.model.event.code.Role;
import com.grepp.spring.app.model.event.dto.*;
import com.grepp.spring.app.model.event.entity.CandidateDate;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.entity.TempSchedule;
import com.grepp.spring.app.model.event.repository.CandidateDateRepository;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.event.repository.TempScheduleRepository;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.group.repository.GroupRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private final TempScheduleRepository tempScheduleRepository;
    private final EventScheduleResultService eventScheduleResultService;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final ScheduleMemberQueryRepository scheduleMemberQueryRepository;

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
            Group tempGroup = createTempGroupForSingleEvent(serviceRequest.getTitle(), serviceRequest.getDescription());
            event = CreateEventDto.toEntity(serviceRequest, tempGroup);
        }

        event = eventRepository.save(event);
        EventMemberDto masterDto = EventMemberDto.toDto(event.getId(), currentMemberId, Role.ROLE_MASTER);
        createEventMember(masterDto);
        createCandidateDates(event, serviceRequest.getCandidateDates());
    }

    private Group createTempGroupForSingleEvent(String eventTitle, String eventDescription) {
        TempGroupCreateDto tempGroupDto = TempGroupCreateDto.forSingleEvent(eventTitle, eventDescription);
        Group tempGroup = TempGroupCreateDto.toEntity(tempGroupDto);

        return groupRepository.save(tempGroup);
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

    @Transactional
    public void createOrUpdateMyTime(MyTimeScheduleRequest request, Long eventId, String currentMemberId) {
        MyTimeScheduleDto dto = MyTimeScheduleDto.toDto(request, eventId, currentMemberId);

        Event event = eventRepository.findById(dto.getEventId())
            .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다. ID: " + dto.getEventId()));

        EventMember eventMember = eventMemberRepository.findByEventIdAndMemberIdAndActivatedTrue(dto.getEventId(), dto.getMemberId())
            .orElseThrow(() -> new NotFoundException("이벤트에 참여하지 않은 회원입니다."));

        for (MyTimeScheduleDto.DailyTimeSlotDto slot : dto.getDailyTimeSlots()) {
            updateOrCreateTempSchedule(eventMember, slot);
        }
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

    @Transactional(readOnly = true)
    public AllTimeScheduleResponse getAllTimeSchedules(Long eventId, String currentMemberId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다. ID: " + eventId));

        if (!eventMemberRepository.existsByEventIdAndMemberId(eventId, currentMemberId)) {
            throw new IllegalStateException("해당 이벤트에 참여하지 않은 사용자입니다.");
        }

        List<CandidateDate> candidateDates = candidateDateRepository
            .findAllByEventIdAndActivatedTrueOrderByDate(eventId);

        List<EventMember> eventMembers = eventMemberRepository
            .findAllByEventIdAndActivatedTrue(eventId);

        Map<Long, List<TempSchedule>> memberScheduleMap = getMemberScheduleMap(eventMembers);

        AllTimeScheduleDto.TimeTableDto timeTable = buildTimeTable(candidateDates);

        List<AllTimeScheduleDto.MemberScheduleDto> memberSchedules = eventMembers.stream()
            .map(member -> buildSingleMemberSchedule(member, candidateDates, memberScheduleMap))
            .collect(Collectors.toList());

        Integer confirmedMembers = (int) eventMembers.stream()
            .filter(EventMember::getConfirmed)
            .count();

        AllTimeScheduleDto dto = AllTimeScheduleDto.builder()
            .eventId(event.getId())
            .eventTitle(event.getTitle())
            .timeTable(timeTable)
            .memberSchedules(memberSchedules)
            .totalMembers(eventMembers.size())
            .confirmedMembers(confirmedMembers)
            .build();

        return AllTimeScheduleDto.fromDto(dto);
    }

    private Map<Long, List<TempSchedule>> getMemberScheduleMap(List<EventMember> eventMembers) {
        List<TempSchedule> allSchedules = tempScheduleRepository
            .findAllByEventMemberInAndActivatedTrueOrderByEventMemberIdAscDateAsc(eventMembers);

        return allSchedules.stream()
            .collect(Collectors.groupingBy(schedule -> schedule.getEventMember().getId()));
    }

    private AllTimeScheduleDto.TimeTableDto buildTimeTable(List<CandidateDate> candidateDates) {
        List<AllTimeScheduleDto.DateInfoDto> dateInfos = candidateDates.stream()
            .map(candidateDate -> {
                LocalDate date = candidateDate.getDate();
                return AllTimeScheduleDto.DateInfoDto.builder()
                    .date(date.toString())
                    .dayOfWeek(AllTimeScheduleDto.formatDayOfWeek(date))
                    .displayDate(AllTimeScheduleDto.formatDisplayDate(date))
                    .build();
            })
            .collect(Collectors.toList());

        CandidateDate firstCandidate = candidateDates.getFirst();
        String startTime = firstCandidate.getStartTime().toString();
        String endTime = firstCandidate.getEndTime().toString();

        return AllTimeScheduleDto.TimeTableDto.builder()
            .dates(dateInfos)
            .startTime(startTime)
            .endTime(endTime)
            .build();
    }

    private AllTimeScheduleDto.MemberScheduleDto buildSingleMemberSchedule(
        EventMember eventMember,
        List<CandidateDate> candidateDates,
        Map<Long, List<TempSchedule>> memberScheduleMap) {

        List<TempSchedule> memberSchedules = memberScheduleMap.getOrDefault(eventMember.getId(), List.of());

        Map<LocalDate, TempSchedule> scheduleByDate = memberSchedules.stream()
            .collect(Collectors.toMap(TempSchedule::getDate, schedule -> schedule));

        List<AllTimeScheduleDto.DailyTimeSlotDto> dailyTimeSlots = candidateDates.stream()
            .map(candidateDate -> {
                LocalDate date = candidateDate.getDate();
                TempSchedule schedule = scheduleByDate.get(date);

                String timeBit = schedule != null ?
                    AllTimeScheduleDto.formatTimeBit(schedule.getTimeBit()) : "000000000000";

                return AllTimeScheduleDto.DailyTimeSlotDto.builder()
                    .date(date.toString())
                    .timeBit(timeBit)
                    .build();
            })
            .collect(Collectors.toList());

        return AllTimeScheduleDto.MemberScheduleDto.builder()
            .eventMemberId(eventMember.getMember().getId())
            .memberName(eventMember.getMember().getName())
            .dailyTimeSlots(dailyTimeSlots)
            .isConfirmed(eventMember.getConfirmed())
            .build();
    }

    @Transactional
    public void completeMyTime(Long eventId, String currentMemberId) {
        JoinEventDto dto = JoinEventDto.toDto(eventId, currentMemberId);

        Event event = eventRepository.findById(dto.getEventId())
            .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다. ID: " + dto.getEventId()));

        EventMember eventMember = eventMemberRepository
            .findByEventIdAndMemberIdAndActivatedTrue(dto.getEventId(), dto.getMemberId())
            .orElseThrow(() -> new NotFoundException("이벤트에 참여하지 않은 회원입니다."));

        List<TempSchedule> schedules = tempScheduleRepository
            .findAllByEventMemberIdAndActivatedTrue(eventMember.getId());

        if (schedules.isEmpty()) {
            throw new IllegalStateException("가능한 시간대를 먼저 입력해주세요.");
        }

        if (eventMember.getConfirmed()) {
            throw new IllegalStateException("이미 확정된 일정입니다.");
        }

        eventMember.setConfirmed(true);
        eventMemberRepository.save(eventMember);
    }

    @Transactional
    public void createScheduleResult(Long eventId, String currentMemberId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다. ID: " + eventId));

        if (!eventMemberRepository.existsByEventIdAndMemberId(eventId, currentMemberId)) {
            throw new IllegalStateException("해당 이벤트에 참여하지 않은 사용자입니다.");
        }

        eventScheduleResultService.createScheduleRecommendations(eventId);
    }

    @Transactional(readOnly = true)
    public ScheduleResultResponse getScheduleResult(Long eventId, String currentMemberId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다. ID: " + eventId));

        if (!eventMemberRepository.existsByEventIdAndMemberId(eventId, currentMemberId)) {
            throw new IllegalStateException("해당 이벤트에 참여하지 않은 사용자입니다.");
        }

        Integer totalParticipants = Math.toIntExact(eventMemberRepository.countByEventId(eventId));

        List<Schedule> recommendSchedules = scheduleQueryRepository.findByEventIdAndStatusInAndActivatedTrue(
            eventId,
            Arrays.asList(ScheduleStatus.L_RECOMMEND, ScheduleStatus.E_RECOMMEND)
        );

        if (recommendSchedules.isEmpty()) {
            throw new IllegalStateException("아직 조율 결과가 생성되지 않았습니다.");
        }

        List<ScheduleResultDto.TimeSlotDetailDto> longestMeetingTimes = findAllRecommendationsByType(
            recommendSchedules, ScheduleStatus.L_RECOMMEND
        );

        List<ScheduleResultDto.TimeSlotDetailDto> earliestMeetingTimes = findAllRecommendationsByType(
            recommendSchedules, ScheduleStatus.E_RECOMMEND
        );

        ScheduleResultDto.RecommendationSummaryDto recommendation = ScheduleResultDto.RecommendationSummaryDto.builder()
            .longestMeetingTimes(longestMeetingTimes)
            .earliestMeetingTimes(earliestMeetingTimes)
            .build();

        ScheduleResultDto dto = ScheduleResultDto.builder()
            .eventTitle(event.getTitle())
            .totalParticipants(totalParticipants)
            .recommendation(recommendation)
            .build();

        return ScheduleResultDto.fromDto(dto);
    }

    private List<ScheduleResultDto.TimeSlotDetailDto> findAllRecommendationsByType(
        List<Schedule> schedules, ScheduleStatus targetStatus) {

        List<Schedule> filteredSchedules = schedules.stream()
            .filter(schedule -> schedule.getStatus() == targetStatus)
            .sorted(Comparator.comparing(Schedule::getId))
            .toList();

        List<ScheduleResultDto.TimeSlotDetailDto> result = new ArrayList<>();
        for (int i = 0; i < filteredSchedules.size(); i++) {
            Schedule schedule = filteredSchedules.get(i);
            result.add(convertScheduleToTimeSlotDetail(schedule, i + 1));
        }

        return result;
    }

    private ScheduleResultDto.TimeSlotDetailDto convertScheduleToTimeSlotDetail(Schedule schedule, int index) {
        List<ScheduleMember> scheduleMembers = scheduleMemberQueryRepository.findByScheduleId(schedule.getId());

        List<ScheduleResultDto.ParticipantDto> participants = scheduleMembers.stream()
            .map(member -> ScheduleResultDto.ParticipantDto.builder()
                .memberId(member.getMember().getId())
                .memberName(member.getMember().getName())
                .build())
            .collect(Collectors.toList());

        boolean isSelectedStatus = schedule.getStatus() == ScheduleStatus.FIXED ||
            schedule.getStatus() == ScheduleStatus.COMPLETE;

        return ScheduleResultDto.TimeSlotDetailDto.builder()
            .startTime(schedule.getStartTime())
            .endTime(schedule.getEndTime())
            .participantCount(participants.size())
            .participants(participants)
            .isSelected(isSelectedStatus)
            .timeSlotId("slot_" + index)
            .build();
    }

}