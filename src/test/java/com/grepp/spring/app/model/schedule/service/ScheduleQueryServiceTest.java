package com.grepp.spring.app.model.schedule.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.grepp.spring.app.controller.api.schedule.payload.response.ShowScheduleResponse;
import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleQueryServiceTest {

    // 테스트 대상 클래스
    @InjectMocks
    private ScheduleQueryService scheduleQueryService;

    @Mock
    private ScheduleMemberQueryRepository scheduleMemberQueryRepository;

    @Test
    void showSchedule_정상_조회_성공() {
        // Given
        Long scheduleId = 1L;

        Event event = mock(Event.class);
        given(event.getMeetingType()).willReturn(MeetingType.ONLINE);

        Schedule schedule = mock(Schedule.class);
        given(schedule.getId()).willReturn(scheduleId);
        given(schedule.getEvent()).willReturn(event);

        // 레포지토리 리턴값 설정
        List<ScheduleMember> fakeMembers = List.of(mock(ScheduleMember.class));
        List<Workspace> fakeWorkspaces = List.of(mock(Workspace.class));

        given(scheduleMemberQueryRepository.findByScheduleId(scheduleId)).willReturn(fakeMembers);

        // When
        ShowScheduleResponse response = scheduleQueryService.showSchedule(schedule);

        // Then
        assertNotNull(response);
        // 세부 검증은 ShowScheduleDto.fromEntity(), fromDto() 내부 구현에 따라 다름
        // 예: assertEquals(eventId, response.getEventId()); 같은 방식으로도 검증 가능
    }
}
