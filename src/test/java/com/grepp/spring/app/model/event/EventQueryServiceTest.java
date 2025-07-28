package com.grepp.spring.app.model.event;

import com.grepp.spring.app.model.event.code.Role;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.event.service.EventQueryService;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.infra.error.exceptions.event.EventNotFoundException;
import com.grepp.spring.infra.error.exceptions.event.NotEventMemberException;
import com.grepp.spring.infra.error.exceptions.event.ScheduleResultNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class EventQueryServiceTest {

    @InjectMocks
    private EventQueryService eventQueryService;

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMemberRepository eventMemberRepository;
    @Mock
    private ScheduleQueryRepository scheduleQueryRepository;

    @Nested
    @DisplayName("이벤트 상세 정보 조회 (getEvent) 테스트")
    class GetEventTests {
        @Test
        @DisplayName("성공: 이벤트 참여자가 상세 정보를 정상적으로 조회한다")
        void getEvent_Success() {
            // given
            Long eventId = 1L;
            String currentMemberId = "GOOGLE_1234";

            Event mockEvent = mock(Event.class);
            Group mockGroup = mock(Group.class);
            EventMember mockEventMember = mock(EventMember.class);

            given(eventRepository.findById(eventId)).willReturn(Optional.of(mockEvent));
            given(eventMemberRepository.findByEventIdAndMemberIdAndActivatedTrue(eventId, currentMemberId))
                .willReturn(Optional.of(mockEventMember));

            given(mockEvent.getId()).willReturn(eventId);
            given(mockEvent.getTitle()).willReturn("Test Event");
            given(mockEvent.getDescription()).willReturn("This is a test event.");
            given(mockEvent.getGroup()).willReturn(mockGroup);
            given(mockEvent.getActivated()).willReturn(true);
            given(mockGroup.getId()).willReturn(10L);
            given(mockEventMember.getRole()).willReturn(Role.ROLE_MEMBER);

            // when
            var response = eventQueryService.getEvent(eventId, currentMemberId);

            // then
            assertThat(response.getEventId()).isEqualTo(eventId);
            assertThat(response.getTitle()).isEqualTo("Test Event");
            assertThat(response.getRole()).isEqualTo("ROLE_MEMBER");
        }

        @Test
        @DisplayName("실패: 존재하지 않는 이벤트를 조회할 수 없다")
        void getEvent_Fail_EventNotFound() {
            // given
            Long nonExistentEventId = 999L;
            String currentMemberId = "GOOGLE_1234";
            given(eventRepository.findById(nonExistentEventId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> eventQueryService.getEvent(nonExistentEventId, currentMemberId))
                .isInstanceOf(EventNotFoundException.class);
        }

        @Test
        @DisplayName("실패: 이벤트에 참여하지 않은 사용자는 조회할 수 없다")
        void getEvent_Fail_NotEventMember() {
            // given
            Long eventId = 1L;
            String notMemberId = "not_a_member";

            Event mockEvent = mock(Event.class);

            given(eventRepository.findById(eventId)).willReturn(Optional.of(mockEvent));
            given(mockEvent.getActivated()).willReturn(true);
            given(eventMemberRepository.findByEventIdAndMemberIdAndActivatedTrue(eventId, notMemberId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> eventQueryService.getEvent(eventId, notMemberId))
                .isInstanceOf(NotEventMemberException.class);
        }
    }


    @Nested
    @DisplayName("이벤트 조율 결과 조회 (getScheduleResult) 테스트")
    class GetScheduleResultTests {
        @Test
        @DisplayName("성공: 추천 스케줄이 생성되었을 때 조율 결과를 정상 조회한다")
        void getScheduleResult_Success() {
            // given
            Long eventId = 1L;
            String currentMemberId = "GOOGLE_1234";

            given(eventRepository.findById(eventId)).willReturn(Optional.of(mock(Event.class)));
            given(eventMemberRepository.findByEventIdAndMemberIdAndActivatedTrue(eventId, currentMemberId))
                .willReturn(Optional.of(mock(EventMember.class)));
            // 추천 스케줄이 존재하는 상황
            given(scheduleQueryRepository.findByEventIdAndStatusInAndActivatedTrue(any(), any()))
                .willReturn(Collections.singletonList(mock(Schedule.class)));

            // when
            var response = eventQueryService.getScheduleResult(eventId, currentMemberId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getRecommendation()).isNotNull();
        }

        @Test
        @DisplayName("실패: 추천 스케줄이 없을 때 조율 결과를 조회하면 예외가 발생한다")
        void getScheduleResult_Fail_NotFound() {
            // given
            Long eventId = 1L;
            String currentMemberId = "GOOGLE_1234";

            given(eventRepository.findById(eventId)).willReturn(Optional.of(mock(Event.class)));
            given(eventMemberRepository.findByEventIdAndMemberIdAndActivatedTrue(eventId, currentMemberId))
                .willReturn(Optional.of(mock(EventMember.class)));
            // 추천 스케줄이 없는 상황
            given(scheduleQueryRepository.findByEventIdAndStatusInAndActivatedTrue(any(), any()))
                .willReturn(Collections.emptyList());

            // when & then
            assertThatThrownBy(() -> eventQueryService.getScheduleResult(eventId, currentMemberId))
                .isInstanceOf(ScheduleResultNotFoundException.class);
        }
    }
}