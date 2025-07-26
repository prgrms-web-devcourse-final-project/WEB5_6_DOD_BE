package com.grepp.spring.app.model.event;

import com.grepp.spring.app.model.event.entity.CandidateDate;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.entity.TempSchedule;
import com.grepp.spring.app.model.event.repository.CandidateDateRepository;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.event.repository.TempScheduleRepository;
import com.grepp.spring.app.model.event.service.EventScheduleResultService;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.infra.error.exceptions.event.EventNotFoundException;
import com.grepp.spring.infra.error.exceptions.event.InvalidEventDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventScheduleResultServiceTest {

    @InjectMocks
    private EventScheduleResultService eventScheduleResultService;

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMemberRepository eventMemberRepository;
    @Mock
    private CandidateDateRepository candidateDateRepository;
    @Mock
    private TempScheduleRepository tempScheduleRepository;
    @Mock
    private ScheduleQueryRepository scheduleQueryRepository;
    @Mock
    private ScheduleMemberQueryRepository scheduleMemberQueryRepository;

    @Test
    @DisplayName("성공: 참여자들의 시간을 분석하여 추천 스케줄을 정상 생성한다")
    void createScheduleRecommendations_Success() {
        // given
        Long eventId = 1L;
        Event mockEvent = mock(Event.class);
        given(mockEvent.getTitle()).willReturn("Test Event");
        given(mockEvent.getDescription()).willReturn("Test Desc");

        // 참여자 2명 설정
        Member memberA = new Member(); memberA.setId("userA"); memberA.setName("User A");
        Member memberB = new Member(); memberB.setId("userB"); memberB.setName("User B");
        EventMember eventMemberA = new EventMember(); eventMemberA.setId(101L); eventMemberA.setMember(memberA);
        EventMember eventMemberB = new EventMember(); eventMemberB.setId(102L); eventMemberB.setMember(memberB);
        List<EventMember> eventMembers = List.of(eventMemberA, eventMemberB);

        // 후보 날짜 설정
        CandidateDate candidateDate = new CandidateDate();
        candidateDate.setDate(LocalDate.now());
        candidateDate.setStartTime(LocalTime.of(10, 0));
        candidateDate.setEndTime(LocalTime.of(12, 0));
        List<CandidateDate> candidateDates = List.of(candidateDate);

        // 각 참여자의 가능한 시간 설정
        TempSchedule scheduleA = new TempSchedule();
        scheduleA.setEventMember(eventMemberA);
        scheduleA.setDate(candidateDate.getDate());
        scheduleA.setTimeBit((1L << 20) | (1L << 21));
        TempSchedule scheduleB = new TempSchedule();
        scheduleB.setEventMember(eventMemberB);
        scheduleB.setDate(candidateDate.getDate());
        scheduleB.setTimeBit((1L << 21) | (1L << 22));

        given(eventRepository.findById(eventId)).willReturn(Optional.of(mockEvent));
        given(eventMemberRepository.findAllByEventIdAndActivatedTrue(eventId)).willReturn(eventMembers);
        given(candidateDateRepository.findAllByEventIdAndActivatedTrueOrderByDate(eventId)).willReturn(candidateDates);
        given(tempScheduleRepository.findAllByEventMemberInAndActivatedTrueOrderByEventMemberIdAscDateAsc(eventMembers))
            .willReturn(List.of(scheduleA, scheduleB));

        // 기존 추천 스케줄은 없다고 가정
        given(scheduleQueryRepository.findByEventIdAndStatusInAndActivatedTrue(any(), any())).willReturn(Collections.emptyList());
        given(scheduleQueryRepository.save(any(Schedule.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        eventScheduleResultService.createScheduleRecommendations(eventId);

        // then
        // 기존 추천 삭제 로직이 호출되었는지 확인
        verify(scheduleQueryRepository).findByEventIdAndStatusInAndActivatedTrue(eq(eventId), anyList());

        // 새로운 추천 스케줄이 저장되었는지 확인 (ArgumentCaptor로 저장된 객체 캡처)
        ArgumentCaptor<Schedule> scheduleCaptor = ArgumentCaptor.forClass(Schedule.class);
        verify(scheduleQueryRepository, atLeastOnce()).save(scheduleCaptor.capture());

        List<Schedule> savedSchedules = scheduleCaptor.getAllValues();
        // 추천 시간대가 생성되었는지 확인
        assertThat(savedSchedules).isNotEmpty();

        // 생성된 스케줄에 멤버가 제대로 포함되었는지 확인
        ArgumentCaptor<ScheduleMember> scheduleMemberCaptor = ArgumentCaptor.forClass(ScheduleMember.class);
        verify(scheduleMemberQueryRepository, atLeastOnce()).save(scheduleMemberCaptor.capture());
    }


    @Test
    @DisplayName("실패: 이벤트가 존재하지 않으면 EventNotFoundException이 발생한다")
    void createScheduleRecommendations_Fail_EventNotFound() {
        // given
        Long nonExistentEventId = 999L;
        given(eventRepository.findById(nonExistentEventId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> eventScheduleResultService.createScheduleRecommendations(nonExistentEventId))
            .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("실패: 후보 날짜가 없으면 InvalidEventDataException이 발생한다")
    void createScheduleRecommendations_Fail_NoCandidateDates() {
        // given
        Long eventId = 1L;
        given(eventRepository.findById(eventId)).willReturn(Optional.of(mock(Event.class)));
        given(candidateDateRepository.findAllByEventIdAndActivatedTrueOrderByDate(eventId)).willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> eventScheduleResultService.createScheduleRecommendations(eventId))
            .isInstanceOf(InvalidEventDataException.class);
    }
}