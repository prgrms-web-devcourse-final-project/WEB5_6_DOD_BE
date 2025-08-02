package com.grepp.spring.app.model.event;

import com.grepp.spring.app.controller.api.event.payload.request.CreateEventRequest;
import com.grepp.spring.app.controller.api.event.payload.request.MyTimeScheduleRequest;
import com.grepp.spring.app.model.event.dto.CreateEventDto;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.entity.TempSchedule;
import com.grepp.spring.app.model.event.factory.EventCreationStrategyFactory;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.event.repository.TempScheduleRepository;
import com.grepp.spring.app.model.event.service.EventCommandService;
import com.grepp.spring.app.model.event.service.EventScheduleCacheService;
import com.grepp.spring.app.model.event.service.EventScheduleResultService;
import com.grepp.spring.app.model.event.strategy.EventCreationStrategy;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.group.repository.GroupRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.infra.error.exceptions.event.AlreadyConfirmedScheduleException;
import com.grepp.spring.infra.error.exceptions.event.AlreadyJoinedEventException;
import com.grepp.spring.infra.error.exceptions.event.InvalidEventDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventCommandServiceTest {

    @InjectMocks
    private EventCommandService eventCommandService;

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMemberRepository eventMemberRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GroupMemberRepository groupMemberRepository;
    @Mock
    private TempScheduleRepository tempScheduleRepository;
    @Mock
    private EventCreationStrategyFactory strategyFactory;
    @Mock
    private EventCreationStrategy eventCreationStrategy;
    @Mock
    private EventScheduleCacheService cacheService;
    @Mock
    private EventScheduleResultService eventScheduleResultService;

    private CreateEventRequest createValidCreateEventRequest() {
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("테스트");
        request.setDescription("설명");
        request.setMeetingType("OFFLINE");
        request.setMaxMember(10);
        CreateEventRequest.CandidateDateWeb dateWeb = new CreateEventRequest.CandidateDateWeb();
        dateWeb.setDates(List.of(LocalDate.now().plusDays(1)));
        dateWeb.setStartTime(LocalTime.of(9, 0));
        dateWeb.setEndTime(LocalTime.of(18, 0));
        request.setDateList(List.of(dateWeb));
        return request;
    }

    @Nested
    @DisplayName("이벤트 생성 (createEvent) 테스트")
    class CreateEventTests {
        @Test
        @DisplayName("성공: 그룹 ID가 있을 경우 그룹 이벤트로 정상 생성된다")
        void createEvent_Success_GroupEvent() {
            // given
            String currentMemberId = "GOOGLE_1234";
            CreateEventRequest webRequest = createValidCreateEventRequest();
            webRequest.setGroupId(10L);
            Event mockEvent = mock(Event.class);
            Group mockGroup = mock(Group.class);

            given(strategyFactory.getStrategy(10L)).willReturn(eventCreationStrategy);
            given(eventCreationStrategy.createEvent(any(CreateEventDto.class), eq(currentMemberId))).willReturn(mockEvent);
            given(mockEvent.getId()).willReturn(1L);
            given(mockEvent.getTitle()).willReturn("그룹 이벤트");
            given(mockEvent.getGroup()).willReturn(mockGroup);
            given(mockGroup.getId()).willReturn(10L);

            // when
            var response = eventCommandService.createEvent(webRequest, currentMemberId);

            // then
            assertThat(response.getEventId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo("그룹 이벤트");
            assertThat(response.getGroupId()).isEqualTo(10L);
            verify(strategyFactory).getStrategy(10L);
            verify(eventCreationStrategy).createEvent(any(CreateEventDto.class), eq(currentMemberId));
        }

        @Test
        @DisplayName("성공: 그룹 ID가 없을 경우 일회성 이벤트로 정상 생성된다")
        void createEvent_Success_SingleEvent() {
            // given
            String currentMemberId = "GOOGLE_1234";
            CreateEventRequest webRequest = createValidCreateEventRequest();
            webRequest.setGroupId(null);
            Event mockEvent = mock(Event.class);
            Group mockTempGroup = mock(Group.class); // 임시 그룹

            given(strategyFactory.getStrategy(null)).willReturn(eventCreationStrategy);
            given(eventCreationStrategy.createEvent(any(CreateEventDto.class), eq(currentMemberId))).willReturn(mockEvent);
            given(mockEvent.getId()).willReturn(2L);
            given(mockEvent.getTitle()).willReturn("일회성 이벤트");
            given(mockEvent.getGroup()).willReturn(mockTempGroup);
            given(mockTempGroup.getId()).willReturn(99L); // 임시 그룹 ID

            // when
            var response = eventCommandService.createEvent(webRequest, currentMemberId);

            // then
            assertThat(response.getEventId()).isEqualTo(2L);
            assertThat(response.getTitle()).isEqualTo("일회성 이벤트");
            verify(strategyFactory).getStrategy(null);
            verify(eventCreationStrategy).createEvent(any(CreateEventDto.class), eq(currentMemberId));
        }
    }


    @Nested
    @DisplayName("이벤트 참여 (joinEvent) 테스트")
    class JoinEventTests {
        @Test
        @DisplayName("성공: 사용자가 이벤트에 정상적으로 참여한다")
        void joinEvent_Success() {
            // given
            Long eventId = 1L;
            Long groupId = 10L;
            String currentMemberId = "GOOGLE_1234";

            Event mockEvent = mock(Event.class);
            Member mockMember = mock(Member.class);

            given(eventRepository.findById(eventId)).willReturn(Optional.of(mockEvent));
            given(eventRepository.findEventForUpdate(eventId)).willReturn(Optional.of(mockEvent));
            given(memberRepository.findById(currentMemberId)).willReturn(Optional.of(mockMember));
            given(eventMemberRepository.existsByEventIdAndMemberId(eventId, currentMemberId)).willReturn(false);
            given(eventMemberRepository.countByEventId(eventId)).willReturn(5L);
            doNothing().when(mockEvent).validateCapacity(5L);

            given(groupRepository.findById(groupId)).willReturn(Optional.of(mock(Group.class)));
            given(groupMemberRepository.findByGroupIdAndMemberId(groupId, currentMemberId)).willReturn(Optional.empty());

            // when
            eventCommandService.joinEvent(eventId, groupId, currentMemberId);

            // then
            verify(eventMemberRepository).save(any(EventMember.class));
            verify(groupMemberRepository).save(any(GroupMember.class));
        }

        @Test
        @DisplayName("실패: 이미 참여한 이벤트에 중복 참여할 수 없다")
        void joinEvent_Fail_AlreadyJoined() {
            // given
            Long eventId = 1L;
            String currentMemberId = "GOOGLE_1234";

            //given(eventRepository.findById(eventId)).willReturn(Optional.of(mock(Event.class)));
            given(eventRepository.findEventForUpdate(eventId)).willReturn(Optional.of(mock(Event.class)));
            given(memberRepository.findById(currentMemberId)).willReturn(Optional.of(mock(Member.class)));
            given(eventMemberRepository.existsByEventIdAndMemberId(eventId, currentMemberId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> eventCommandService.joinEvent(eventId, 10L, currentMemberId))
                .isInstanceOf(AlreadyJoinedEventException.class);

            verify(eventMemberRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 이벤트 정원이 가득 차면 참여할 수 없다")
        void joinEvent_Fail_CapacityExceeded() {
            // given
            Long eventId = 1L;
            String currentMemberId = "GOOGLE_1234";
            Event mockEvent = mock(Event.class);

            //given(eventRepository.findById(eventId)).willReturn(Optional.of(mockEvent));
            given(eventRepository.findEventForUpdate(eventId)).willReturn(Optional.of(mockEvent));
            given(memberRepository.findById(currentMemberId)).willReturn(Optional.of(mock(Member.class)));
            given(eventMemberRepository.existsByEventIdAndMemberId(eventId, currentMemberId)).willReturn(false);
            given(eventMemberRepository.countByEventId(eventId)).willReturn(10L);
            // validateCapacity 호출 시 예외 발생하도록 설정
            doThrow(InvalidEventDataException.class).when(mockEvent).validateCapacity(10L);

            // when & then
            assertThatThrownBy(() -> eventCommandService.joinEvent(eventId, 10L, currentMemberId))
                .isInstanceOf(InvalidEventDataException.class);
        }
    }


    @Nested
    @DisplayName("개인 시간 제출/확정 테스트")
    class MyTimeTests {
        @Test
        @DisplayName("성공: 개인 가능 시간을 최초로 제출한다")
        void createOrUpdateMyTime_Success_Create() {
            // given
            Long eventId = 1L;
            String currentMemberId = "GOOGLE_1234";
            MyTimeScheduleRequest request = new MyTimeScheduleRequest();
            MyTimeScheduleRequest.DailyTimeSlot slot = new MyTimeScheduleRequest.DailyTimeSlot();
            slot.setDate(LocalDate.now());
            slot.setTimeBit("FC0000");
            request.setDailyTimeSlots(List.of(slot));

            EventMember mockEventMember = mock(EventMember.class);
            given(eventRepository.findById(eventId)).willReturn(Optional.of(mock(Event.class)));
            given(eventMemberRepository.findByEventIdAndMemberIdAndActivatedTrue(eventId, currentMemberId))
                .willReturn(Optional.of(mockEventMember));
            given(mockEventMember.getConfirmed()).willReturn(false); // 아직 확정 안 함
            given(tempScheduleRepository.findByEventMemberIdAndDateAndActivatedTrue(any(), any())).willReturn(Optional.empty()); // 기존 데이터 없음

            // when
            eventCommandService.createOrUpdateMyTime(request, eventId, currentMemberId);

            // then
            verify(tempScheduleRepository).save(any(TempSchedule.class));
        }

        @Test
        @DisplayName("실패: 이미 확정한 사용자는 시간을 수정할 수 없다")
        void createOrUpdateMyTime_Fail_AlreadyConfirmed() {
            // given
            Long eventId = 1L;
            String currentMemberId = "GOOGLE_1234";
            MyTimeScheduleRequest request = new MyTimeScheduleRequest();
            request.setDailyTimeSlots(Collections.emptyList()); // Null 방지

            EventMember mockEventMember = mock(EventMember.class);
            given(eventRepository.findById(eventId)).willReturn(Optional.of(mock(Event.class)));
            given(eventMemberRepository.findByEventIdAndMemberIdAndActivatedTrue(eventId, currentMemberId))
                .willReturn(Optional.of(mockEventMember));
            given(mockEventMember.getConfirmed()).willReturn(true); // 이미 확정한 상태

            // when & then
            assertThatThrownBy(() -> eventCommandService.createOrUpdateMyTime(request, eventId, currentMemberId))
                .isInstanceOf(AlreadyConfirmedScheduleException.class);
        }

        @Test
        @DisplayName("성공: 제출한 시간이 있으면 정상적으로 확정한다")
        void completeMyTime_Success() {
            // given
            Long eventId = 1L;
            String currentMemberId = "GOOGLE_1234";
            EventMember mockEventMember = mock(EventMember.class);

            given(eventRepository.findById(eventId)).willReturn(Optional.of(mock(Event.class)));
            given(eventMemberRepository.findByEventIdAndMemberIdAndActivatedTrue(eventId, currentMemberId))
                .willReturn(Optional.of(mockEventMember));
            given(tempScheduleRepository.findAllByEventMemberIdAndActivatedTrue(any())).willReturn(List.of(new TempSchedule())); // 제출한 시간 있음
            doNothing().when(mockEventMember).confirmScheduleOrThrow();

            // when
            eventCommandService.completeMyTime(eventId, currentMemberId);

            // then
            verify(mockEventMember).confirmScheduleOrThrow();
            verify(eventMemberRepository).save(mockEventMember);
        }

        @Test
        @DisplayName("실패: 제출한 시간이 없으면 확정할 수 없다")
        void completeMyTime_Fail_NoTimeSubmitted() {
            // given
            Long eventId = 1L;
            String currentMemberId = "GOOGLE_1234";
            EventMember mockEventMember = mock(EventMember.class);

            given(eventRepository.findById(eventId)).willReturn(Optional.of(mock(Event.class)));
            given(eventMemberRepository.findByEventIdAndMemberIdAndActivatedTrue(eventId, currentMemberId))
                .willReturn(Optional.of(mockEventMember));
            given(tempScheduleRepository.findAllByEventMemberIdAndActivatedTrue(any())).willReturn(List.of()); // 제출한 시간 없음

            // when & then
            assertThatThrownBy(() -> eventCommandService.completeMyTime(eventId, currentMemberId))
                .isInstanceOf(InvalidEventDataException.class);
        }
    }
}