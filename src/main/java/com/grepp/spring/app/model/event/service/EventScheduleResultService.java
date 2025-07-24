package com.grepp.spring.app.model.event.service;

import com.grepp.spring.app.model.event.dto.RecommendationDto;
import com.grepp.spring.app.model.event.entity.CandidateDate;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.entity.TempSchedule;
import com.grepp.spring.app.model.event.repository.CandidateDateRepository;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.event.repository.TempScheduleRepository;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.infra.error.exceptions.event.EventNotFoundException;
import com.grepp.spring.infra.error.exceptions.event.InvalidEventDataException;
import com.grepp.spring.infra.response.EventErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventScheduleResultService {

    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final CandidateDateRepository candidateDateRepository;
    private final TempScheduleRepository tempScheduleRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final ScheduleMemberQueryRepository scheduleMemberQueryRepository;

    @Transactional
    public void createScheduleRecommendations(Long eventId) {
        deleteExistingRecommendations(eventId);

        Event event = validateAndGetEvent(eventId);
        List<EventMember> eventMembers = eventMemberRepository.findAllByEventIdAndActivatedTrue(eventId);
        List<CandidateDate> candidateDates = getCandidateDates(eventId);
        Map<Long, List<TempSchedule>> memberScheduleMap = getMemberScheduleMap(eventMembers);

        if (memberScheduleMap.isEmpty()) {
            throw new InvalidEventDataException(EventErrorCode.CANNOT_CREATE_SCHEDULE_RESULT);
        }

        List<RecommendationDto.TimeSlot> availableTimeSlots = analyzeAvailableTimeSlots(candidateDates, eventMembers, memberScheduleMap);

        List<RecommendationDto.ContinuousTimeRange> continuousRanges = mergeContinuousTimeSlots(availableTimeSlots);

        List<RecommendationDto.RecommendationInfo> recommendations = generateRecommendations(continuousRanges);

        saveRecommendationsAsSchedules(event, recommendations);
    }

    private void deleteExistingRecommendations(Long eventId) {
        List<Schedule> existingRecommendations = scheduleQueryRepository
            .findByEventIdAndStatusInAndActivatedTrue(
                eventId,
                Arrays.asList(ScheduleStatus.L_RECOMMEND, ScheduleStatus.E_RECOMMEND)
            );

        if (!existingRecommendations.isEmpty()) {
            for (Schedule schedule : existingRecommendations) {
                List<ScheduleMember> scheduleMembers = scheduleMemberQueryRepository.findByScheduleId(schedule.getId());
                if (!scheduleMembers.isEmpty()) {
                    scheduleMemberQueryRepository.deleteAll(scheduleMembers);
                }
            }

            scheduleQueryRepository.deleteAll(existingRecommendations);
        }
    }

    private Event validateAndGetEvent(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(EventErrorCode.EVENT_NOT_FOUND));
    }

    private List<CandidateDate> getCandidateDates(Long eventId) {
        List<CandidateDate> candidateDates = candidateDateRepository.findAllByEventIdAndActivatedTrueOrderByDate(eventId);
        if (candidateDates.isEmpty()) {
            throw new InvalidEventDataException(EventErrorCode.INVALID_CANDIDATE_DATES);
        }

        return candidateDates;
    }

    private Map<Long, List<TempSchedule>> getMemberScheduleMap(List<EventMember> eventMembers) {
        List<TempSchedule> allSchedules = tempScheduleRepository
            .findAllByEventMemberInAndActivatedTrueOrderByEventMemberIdAscDateAsc(eventMembers);

        return allSchedules.stream()
            .collect(Collectors.groupingBy(schedule -> schedule.getEventMember().getId()));
    }

    private List<RecommendationDto.TimeSlot> analyzeAvailableTimeSlots(
        List<CandidateDate> candidateDates,
        List<EventMember> eventMembers,
        Map<Long, List<TempSchedule>> memberScheduleMap) {

        List<RecommendationDto.TimeSlot> timeSlots = new ArrayList<>();

        for (CandidateDate candidateDate : candidateDates) {
            LocalDate date = candidateDate.getDate();
            LocalTime startTime = candidateDate.getStartTime();
            LocalTime endTime = candidateDate.getEndTime();

            LocalTime currentTime = startTime;
            while (currentTime.isBefore(endTime)) {
                LocalTime slotEndTime = currentTime.plusMinutes(30);
                if (slotEndTime.isAfter(endTime)) {
                    break;
                }

                Set<EventMember> availableEventMembers = calculateAvailableEventMembersForSlot(
                    date, currentTime, eventMembers, memberScheduleMap);

                if (!availableEventMembers.isEmpty()) {
                    Set<String> memberNames = availableEventMembers.stream()
                        .map(em -> em.getMember().getName())
                        .collect(Collectors.toSet());

                    timeSlots.add(RecommendationDto.TimeSlot.builder()
                        .date(date)
                        .startTime(currentTime)
                        .endTime(slotEndTime)
                        .availableMembers(memberNames)
                        .availableEventMembers(availableEventMembers)
                        .participantCount(availableEventMembers.size())
                        .build());
                }

                currentTime = currentTime.plusMinutes(30);
            }
        }

        return timeSlots;
    }

    private Set<EventMember> calculateAvailableEventMembersForSlot(
        LocalDate date,
        LocalTime slotStartTime,
        List<EventMember> eventMembers,
        Map<Long, List<TempSchedule>> memberScheduleMap) {

        Set<EventMember> availableEventMembers = new HashSet<>();

        for (EventMember eventMember : eventMembers) {
            List<TempSchedule> memberSchedules = memberScheduleMap.getOrDefault(eventMember.getId(), List.of());

            Optional<TempSchedule> daySchedule = memberSchedules.stream()
                .filter(schedule -> schedule.getDate().equals(date))
                .findFirst();

            if (daySchedule.isPresent()) {
                Long timeBit = daySchedule.get().getTimeBit();
                if (isTimeSlotAvailable(timeBit, slotStartTime)) {
                    availableEventMembers.add(eventMember);
                }
            }
        }

        return availableEventMembers;
    }

    private boolean isTimeSlotAvailable(Long timeBit, LocalTime slotStartTime) {
        if (timeBit == null || timeBit == 0) {
            return false;
        }

        int hour = slotStartTime.getHour();
        int minute = slotStartTime.getMinute();
        int bitIndex = hour * 2 + (minute / 30);

        return (timeBit & (1L << bitIndex)) != 0;
    }

    private List<RecommendationDto.ContinuousTimeRange> mergeContinuousTimeSlots(List<RecommendationDto.TimeSlot> timeSlots) {
        if (timeSlots.isEmpty()) {
            return new ArrayList<>();
        }

        List<RecommendationDto.ContinuousTimeRange> ranges = new ArrayList<>();

        List<RecommendationDto.TimeSlot> sortedSlots = timeSlots.stream()
            .sorted(Comparator.comparing(RecommendationDto.TimeSlot::getDate).thenComparing(RecommendationDto.TimeSlot::getStartTime))
            .toList();

        RecommendationDto.TimeSlot rangeStart = sortedSlots.getFirst();
        RecommendationDto.TimeSlot rangeEnd = sortedSlots.getFirst();

        for (int i = 1; i < sortedSlots.size(); i++) {
            RecommendationDto.TimeSlot currentSlot = sortedSlots.get(i);

            boolean isContinuous = currentSlot.getDate().equals(rangeEnd.getDate()) &&
                currentSlot.getStartTime().equals(rangeEnd.getEndTime()) &&
                currentSlot.getAvailableEventMembers().equals(rangeEnd.getAvailableEventMembers());

            if (isContinuous) {
                rangeEnd = currentSlot;
            } else {
                ranges.add(createContinuousTimeRange(rangeStart, rangeEnd));

                rangeStart = currentSlot;
                rangeEnd = currentSlot;
            }
        }

        ranges.add(createContinuousTimeRange(rangeStart, rangeEnd));

        return ranges;
    }

    private RecommendationDto.ContinuousTimeRange createContinuousTimeRange(RecommendationDto.TimeSlot start, RecommendationDto.TimeSlot end) {
        long durationMinutes = java.time.Duration.between(
            LocalDateTime.of(start.getDate(), start.getStartTime()),
            LocalDateTime.of(end.getDate(), end.getEndTime())
        ).toMinutes();

        return RecommendationDto.ContinuousTimeRange.builder()
            .date(start.getDate())
            .startTime(start.getStartTime())
            .endTime(end.getEndTime())
            .availableMembers(start.getAvailableMembers())
            .availableEventMembers(start.getAvailableEventMembers())
            .participantCount(start.getParticipantCount())
            .durationMinutes(durationMinutes)
            .build();
    }

    private List<RecommendationDto.RecommendationInfo> generateRecommendations(List<RecommendationDto.ContinuousTimeRange> ranges) {
        List<RecommendationDto.RecommendationInfo> recommendations = new ArrayList<>();

        if (ranges.isEmpty()) {
            return recommendations;
        }

        List<RecommendationDto.ContinuousTimeRange> earliestRanges = ranges.stream()
            .sorted((a, b) -> {
                int participantCompare = Integer.compare(b.getParticipantCount(), a.getParticipantCount());
                if (participantCompare != 0) return participantCompare;

                int dateCompare = a.getDate().compareTo(b.getDate());
                if (dateCompare != 0) return dateCompare;

                return a.getStartTime().compareTo(b.getStartTime());
            })
            .limit(3)
            .toList();

        for (RecommendationDto.ContinuousTimeRange range : earliestRanges) {
            recommendations.add(RecommendationDto.RecommendationInfo.builder()
                .startDateTime(LocalDateTime.of(range.getDate(), range.getStartTime()))
                .endDateTime(LocalDateTime.of(range.getDate(), range.getEndTime()))
                .participantCount(range.getParticipantCount())
                .availableMembers(range.getAvailableMembers())
                .availableEventMembers(range.getAvailableEventMembers())
                .type(String.valueOf(ScheduleStatus.E_RECOMMEND))
                .build());
        }

        List<RecommendationDto.ContinuousTimeRange> longestRanges = ranges.stream()
            .sorted((a, b) -> {
                int participantCompare = Integer.compare(b.getParticipantCount(), a.getParticipantCount());
                if (participantCompare != 0) return participantCompare;

                return Long.compare(b.getDurationMinutes(), a.getDurationMinutes());
            })
            .limit(3)
            .toList();

        for (RecommendationDto.ContinuousTimeRange range : longestRanges) {
            recommendations.add(RecommendationDto.RecommendationInfo.builder()
                .startDateTime(LocalDateTime.of(range.getDate(), range.getStartTime()))
                .endDateTime(LocalDateTime.of(range.getDate(), range.getEndTime()))
                .participantCount(range.getParticipantCount())
                .availableMembers(range.getAvailableMembers())
                .availableEventMembers(range.getAvailableEventMembers())
                .type(String.valueOf(ScheduleStatus.L_RECOMMEND))
                .build());
        }

        return recommendations;
    }

    private void saveRecommendationsAsSchedules(Event event, List<RecommendationDto.RecommendationInfo> recommendations) {
        for (RecommendationDto.RecommendationInfo recommendation : recommendations) {
            ScheduleStatus status = recommendation.getType().equals("E_RECOMMEND")
                ? ScheduleStatus.E_RECOMMEND
                : ScheduleStatus.L_RECOMMEND;

            Schedule schedule = Schedule.builder()
                .event(event)
                .startTime(recommendation.getStartDateTime())
                .endTime(recommendation.getEndDateTime())
                .status(status)
                .scheduleName(event.getTitle())
                .description(event.getDescription())
                .build();

            Schedule savedSchedule = scheduleQueryRepository.save(schedule);

            for (EventMember eventMember : recommendation.getAvailableEventMembers()) {
                ScheduleMember scheduleMember = ScheduleMember.builder()
                    .schedule(savedSchedule)
                    .member(eventMember.getMember())
                    .role(ScheduleRole.ROLE_MEMBER)
                    .name(eventMember.getMember().getName())
                    .build();

                scheduleMemberQueryRepository.save(scheduleMember);
            }
        }
    }

}