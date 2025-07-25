//package com.grepp.spring.app.model.schedule.service;
//
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.internal.verification.VerificationModeFactory.times;
//
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//
//import com.grepp.spring.app.controller.api.schedule.payload.request.CreateSchedulesRequest;
//import com.grepp.spring.app.controller.api.schedule.payload.response.CreateSchedulesResponse;
//import com.grepp.spring.app.model.event.code.MeetingType;
//import com.grepp.spring.app.model.event.entity.Event;
//import com.grepp.spring.app.model.schedule.code.ScheduleRole;
//import com.grepp.spring.app.model.schedule.dto.ScheduleMemberRolesDto;
//import com.grepp.spring.app.model.schedule.entity.Schedule;
//import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
//import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
//import java.time.LocalDateTime;
//import java.util.List;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//// public을 중심으로 테스트하는것이 원칙
//// private → public 으로 바꾸고 다시 되돌리면서 테스트하지 않음
//// void 리턴 메서드는 테스트는 verify를 이용하여 정해진 횟수만큼 호출되었는지 확인
//
//@ExtendWith(MockitoExtension.class)
//class ScheduleCommandServiceTest {
//
//    // 테스트 대상 클래스
//    @InjectMocks
//    private ScheduleCommandService scheduleCommandService;
//
//    @Mock
//    private ScheduleMemberQueryRepository scheduleMemberQueryRepository;
//
//    @Test
//    @DisplayName("일정 저장 - 일정 저장 테스트")
//    void createTest() {
//        // given
//        Event eventId = mock(Event.class);
//
//        List<ScheduleMemberRolesDto> memberRoles = List.of(
//            ScheduleMemberRolesDto.builder()
//                .memberId("GOOGLE_1234")
//                .role(ScheduleRole.ROLE_MASTER)
//                .build(),
//            ScheduleMemberRolesDto.builder()
//                .memberId("GOOGLE_5678")
//                .role(ScheduleRole.ROLE_MEMBER)
//                .build()
//        );
//
//        CreateSchedulesRequest createSchedulesRequest = CreateSchedulesRequest.builder()
//            .startTime(LocalDateTime.parse("2025-07-25T07:21:26.740"))
//            .endTime(LocalDateTime.parse("2025-07-26T07:21:26.740"))
//            .meetingType(MeetingType.OFFLINE)
//            .scheduleName("테스트 일정")
//            .description("일정 상세설명")
//            .memberRoles(memberRoles)
//            .build();
//
//        // when
//        Schedule schedule = scheduleCommandService.create(createSchedulesRequest, eventId);
//
//        // then
//        assertNotNull(schedule);
//        Assertions.assertThat(schedule.getScheduleName()).isEqualTo("테스트 일정");
//    }
//
//    @Test
//    @DisplayName("일정 저장 - 일정 멤버 저장 테스트")
//    void createScheduleMembersTest() {
//        // given
//        Schedule schedule = mock(Schedule.class);
//
//        List<ScheduleMemberRolesDto> memberRoles = List.of(
//            ScheduleMemberRolesDto.builder()
//                .memberId("GOOGLE_1234")
//                .role(ScheduleRole.ROLE_MASTER)
//                .build(),
//            ScheduleMemberRolesDto.builder()
//                .memberId("GOOGLE_5678")
//                .role(ScheduleRole.ROLE_MEMBER)
//                .build()
//        );
//
//        CreateSchedulesRequest createSchedulesRequest = CreateSchedulesRequest.builder()
//            .startTime(LocalDateTime.parse("2025-07-25T07:21:26.740"))
//            .endTime(LocalDateTime.parse("2025-07-26T07:21:26.740"))
//            .meetingType(MeetingType.OFFLINE)
//            .scheduleName("테스트 일정")
//            .description("일정 상세설명")
//            .memberRoles(memberRoles)
//            .build();
//
//        // when
//        scheduleCommandService.createScheduleMembers(createSchedulesRequest, schedule);
//
//        // then
//        verify(scheduleMemberQueryRepository, times(2)).save(any(ScheduleMember.class));
//    }
//
//    @Test
//    @DisplayName("일정 저장 - 전체 테스트")
//    void createSchedule() {
//        // given
//        Event eventId = mock(Event.class);
//
//        List<ScheduleMemberRolesDto> memberRoles = List.of(
//            ScheduleMemberRolesDto.builder()
//                .memberId("GOOGLE_1234")
//                .role(ScheduleRole.ROLE_MASTER)
//                .build(),
//            ScheduleMemberRolesDto.builder()
//                .memberId("GOOGLE_5678")
//                .role(ScheduleRole.ROLE_MEMBER)
//                .build()
//        );
//
//        CreateSchedulesRequest createSchedulesRequest = CreateSchedulesRequest.builder()
//            .startTime(LocalDateTime.parse("2025-07-25T07:21:26.740"))
//            .endTime(LocalDateTime.parse("2025-07-26T07:21:26.740"))
//            .meetingType(MeetingType.OFFLINE)
//            .scheduleName("테스트 일정")
//            .description("일정 상세설명")
//            .memberRoles(memberRoles)
//            .build();
//
//        // when
//        CreateSchedulesResponse response = scheduleCommandService.createSchedule(createSchedulesRequest, eventId);
//
//        // then
//        assertNotNull(response);
//    }
//
//}