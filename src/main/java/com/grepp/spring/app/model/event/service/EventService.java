package com.grepp.spring.app.model.event.service;

import com.grepp.spring.app.controller.api.event.payload.request.CreateEventRequest;
import com.grepp.spring.app.controller.api.event.payload.request.MyTimeScheduleRequest;
import com.grepp.spring.app.controller.api.event.payload.response.AllTimeScheduleResponse;
import com.grepp.spring.app.controller.api.event.payload.response.CreateEventResponse;
import com.grepp.spring.app.controller.api.event.payload.response.ScheduleResultResponse;
import com.grepp.spring.app.controller.api.event.payload.response.ShowEventResponse;
import com.grepp.spring.app.model.event.code.Role;
import com.grepp.spring.app.model.event.dto.*;
import com.grepp.spring.app.model.event.entity.CandidateDate;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.entity.TempSchedule;
import com.grepp.spring.app.model.event.factory.EventCreationStrategyFactory;
import com.grepp.spring.app.model.event.repository.CandidateDateRepository;
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
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.infra.error.exceptions.event.*;
import com.grepp.spring.infra.response.EventErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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

    private final EventCreationStrategyFactory strategyFactory;

    @Transactional
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

    @Transactional(readOnly = true)
    public ShowEventResponse getEvent(Long eventId, String currentMemberId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.EVENT_NOT_FOUND));

        EventMember eventMember = eventMemberRepository.findByEventIdAndMemberIdAndActivatedTrue(eventId, currentMemberId)
            .orElseThrow(() -> new NotEventMemberException(EventErrorCode.NOT_EVENT_MEMBER));

        ShowEventResponse response = new ShowEventResponse();
        response.setEventId(event.getId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setRole(eventMember.getRole().name());
        response.setGroupId(event.getGroup().getId());

        return response;
    }

    @Transactional
    public void joinEvent(Long eventId, Long groupId, String currentMemberId) {

        JoinEventDto dto = JoinEventDto.toDto(eventId, currentMemberId);

        Event event = eventRepository.findById(dto.getEventId())
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.EVENT_NOT_FOUND));

        Member member = memberRepository.findById(dto.getMemberId())
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.MEMBER_NOT_FOUND));

        if (eventMemberRepository.existsByEventIdAndMemberId(dto.getEventId(), dto.getMemberId())) {
            throw new AlreadyJoinedEventException(EventErrorCode.ALREADY_JOINED_EVENT);
        }

        Long currentMemberCount = eventMemberRepository.countByEventId(dto.getEventId());
        validateEventCapacity(event, currentMemberCount);

        addUserToGroup(groupId, dto.getMemberId());

        EventMemberDto memberDto = EventMemberDto.toDto(dto.getEventId(), dto.getMemberId(), Role.ROLE_MEMBER);
        createEventMember(memberDto);

    }

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

    private void validateEventCapacity(Event event, Long currentMemberCount) {
        if (event.getMaxMember() != null && currentMemberCount >= event.getMaxMember()) {
            throw new InvalidEventDataException(EventErrorCode.EVENT_MEMBER_LIMIT_EXCEEDED);
        }
    }

    @Transactional
    public void createEventMember(EventMemberDto dto) {
        Event event = eventRepository.findById(dto.getEventId())
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.EVENT_NOT_FOUND));

        Member member = memberRepository.findById(dto.getMemberId())
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.MEMBER_NOT_FOUND));

        EventMember eventMember = EventMemberDto.toEntity(dto, event, member);
        eventMemberRepository.save(eventMember);
    }

    @Transactional
    public void createOrUpdateMyTime(MyTimeScheduleRequest request, Long eventId, String currentMemberId) {
        MyTimeScheduleDto dto = MyTimeScheduleDto.toDto(request, eventId, currentMemberId);

        Event event = eventRepository.findById(dto.getEventId())
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.EVENT_NOT_FOUND));

        EventMember eventMember = eventMemberRepository.findByEventIdAndMemberIdAndActivatedTrue(dto.getEventId(), dto.getMemberId())
            .orElseThrow(() -> new NotEventMemberException(EventErrorCode.NOT_EVENT_MEMBER));

        if (eventMember.getConfirmed()) {
            throw new AlreadyCompletedScheduleException(EventErrorCode.ALREADY_COMPLETED_SCHEDULE);
        }

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
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.EVENT_NOT_FOUND));

        if (!eventMemberRepository.existsByEventIdAndMemberId(eventId, currentMemberId)) {
            throw new NotEventMemberException(EventErrorCode.NOT_EVENT_MEMBER);
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

        Map<String, List<Integer>> participantCounts = calculateParticipantCounts(candidateDates, memberScheduleMap);

        AllTimeScheduleDto dto = AllTimeScheduleDto.builder()
            .eventId(event.getId())
            .eventTitle(event.getTitle())
            .description(event.getDescription())
            .timeTable(timeTable)
            .memberSchedules(memberSchedules)
            .totalMembers(eventMembers.size())
            .confirmedMembers(confirmedMembers)
            .participantCounts(participantCounts)
            .build();

        return AllTimeScheduleDto.fromDto(dto);
    }


    private Map<String, List<Integer>> calculateParticipantCounts(
        List<CandidateDate> candidateDates,
        Map<Long, List<TempSchedule>> memberScheduleMap
    ) {
        Map<String, List<Integer>> participantCounts = new HashMap<>();

        CandidateDate firstCandidate = candidateDates.getFirst();
        LocalTime startTime = firstCandidate.getStartTime();
        LocalTime endTime = firstCandidate.getEndTime();

        int startSlotIndex = startTime.getHour() * 2 + (startTime.getMinute() >= 30 ? 1 : 0);
        int endSlotIndex = endTime.getHour() * 2 + (endTime.getMinute() >= 30 ? 1 : 0);
        int totalSlots = endSlotIndex - startSlotIndex;

        for (CandidateDate candidateDate : candidateDates) {
            LocalDate date = candidateDate.getDate();
            String dateKey = date.toString();

            int[] timeSlotCounts = new int[totalSlots];

            for (List<TempSchedule> memberSchedules : memberScheduleMap.values()) {
                TempSchedule dateSchedule = memberSchedules.stream()
                    .filter(schedule -> schedule.getDate().equals(date))
                    .findFirst()
                    .orElse(null);

                if (dateSchedule != null && dateSchedule.getTimeBit() != null) {
                    long timeBit = dateSchedule.getTimeBit();

                    for (int i = 0; i < totalSlots; i++) {
                        int actualBitIndex = startSlotIndex + i;

                        if (actualBitIndex < 48 && (timeBit & (1L << actualBitIndex)) != 0) {
                            timeSlotCounts[i]++;
                        }
                    }
                }
            }

            List<Integer> participantList = Arrays.stream(timeSlotCounts)
                .boxed()
                .collect(Collectors.toList());

            participantCounts.put(dateKey, participantList);

        }

        return participantCounts;
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
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.EVENT_NOT_FOUND));

        EventMember eventMember = eventMemberRepository
            .findByEventIdAndMemberIdAndActivatedTrue(dto.getEventId(), dto.getMemberId())
            .orElseThrow(() -> new NotEventMemberException(EventErrorCode.NOT_EVENT_MEMBER));

        List<TempSchedule> schedules = tempScheduleRepository
            .findAllByEventMemberIdAndActivatedTrue(eventMember.getId());

        if (schedules.isEmpty()) {
            throw new InvalidEventDataException(EventErrorCode.CANNOT_COMPLETE_EMPTY_SCHEDULE);
        }

        if (eventMember.getConfirmed()) {
            throw new AlreadyCompletedScheduleException(EventErrorCode.ALREADY_COMPLETED_SCHEDULE);
        }

        eventMember.setConfirmed(true);
        eventMemberRepository.save(eventMember);
    }

    @Transactional
    public void createScheduleResult(Long eventId, String currentMemberId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.EVENT_NOT_FOUND));

        EventMember eventMember = eventMemberRepository.findByEventIdAndMemberIdAndActivatedTrue(eventId, currentMemberId)
            .orElseThrow(() -> new NotEventMemberException(EventErrorCode.NOT_EVENT_MEMBER));

        eventScheduleResultService.createScheduleRecommendations(eventId);
    }

    @Transactional(readOnly = true)
    public ScheduleResultResponse getScheduleResult(Long eventId, String currentMemberId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.EVENT_NOT_FOUND));

        EventMember eventMember = eventMemberRepository.findByEventIdAndMemberIdAndActivatedTrue(eventId, currentMemberId)
            .orElseThrow(() -> new NotEventMemberException(EventErrorCode.NOT_EVENT_MEMBER));

        Integer totalParticipants = Math.toIntExact(eventMemberRepository.countByEventId(eventId));

        List<Schedule> recommendSchedules = scheduleQueryRepository.findByEventIdAndStatusInAndActivatedTrue(
            eventId,
            Arrays.asList(ScheduleStatus.L_RECOMMEND, ScheduleStatus.E_RECOMMEND)
        );

        if (recommendSchedules.isEmpty()) {
            throw new ScheduleResultNotFoundException(EventErrorCode.SCHEDULE_RESULT_NOT_FOUND);
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