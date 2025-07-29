package com.grepp.spring.app.model.schedule;

import com.grepp.spring.app.controller.api.schedule.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.service.EventCommandService;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.dto.CreateScheduleMembersDto;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.app.model.schedule.service.ScheduleCommandService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ScheduleCreateTest {

    @Autowired
    private ScheduleCommandService scheduleCommandService;

    @Autowired
    private ScheduleCommandRepository scheduleCommandRepository;

    @Autowired
    private EventCommandService eventCommandService;

    @Autowired
    private EventMemberRepository eventMemberRepository;

    @Autowired
    private ScheduleQueryRepository scheduleQueryRepository;


    @Autowired
    private ScheduleMemberCommandRepository scheduleMemberCommandRepository;


    @BeforeEach
    void setUp() {
        Long eventId = 4L;
        Long groupId = 1L;

        String currentMemberId = "";
        for (int i = 1; i < 11; i++) {
            currentMemberId = i + "a";
            eventCommandService.joinEvent(eventId, groupId, currentMemberId);
        }
    }

    @AfterEach
    void restore() {
        scheduleMemberCommandRepository.deleteAllInBatch();
        scheduleCommandRepository.deleteAllInBatch();
        eventMemberRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("일정 생성 (create) 테스트")
    class CreateScheduleTests {

        @Test
        @DisplayName("멀티 스레드, 10명이 동시에 추가")
        void multiRegistSchedule() throws InterruptedException {

            int threadCount = 10;
            Long eventId = 4L;

            CreateSchedulesRequest request = CreateSchedulesRequest.builder()
                .eventId(eventId)
                .startTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0)
                    .withNano(0))
                .endTime(LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0)
                    .withNano(0))
                .scheduleName("일정" + "1")
                .description("무슨 일정? 일정" + "1")
                .schedulesStatus(ScheduleStatus.L_RECOMMEND)
                .meetingType(MeetingType.ONLINE)
                .members(List.of(CreateScheduleMembersDto.builder().memberId("1a").build()))
                .build();

            // 스레드 풀과 동기화용 래치
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            IntStream.rangeClosed(1, threadCount).forEach(i -> {
                executor.submit(() -> {
                    try {
                        String currentMemberId = i + "a"; // "1a", "2a", ..., "100a"
                        scheduleCommandService.createSchedule(request, currentMemberId);
                    } catch (Exception e) {
                        // 예외는 무시 (정원 초과 등)
                        System.out.println(
                            "Thread " + i + " failed: " + e.getClass().getSimpleName());
                    } finally {
                        latch.countDown();
                    }
                });
            });

            // 모든 쓰레드 종료 대기
            latch.await();
            executor.shutdown();

            List<Schedule> schedules = scheduleQueryRepository.findAll();
            System.out.println(schedules.size());
            assertEquals(1, schedules.size());
            assertEquals("일정1", schedules.get(0).getScheduleName());
        }


        @Test
        @DisplayName("단일 스레드, 일정 생성 테스트")
        void singleRegistSchedule() {

            // given
            Long eventId = 4L;
            String memberId = "1a";
            CreateSchedulesRequest request = CreateSchedulesRequest.builder()
                .eventId(eventId)
                .startTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0)
                    .withNano(0))
                .endTime(LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0)
                    .withNano(0))
                .scheduleName("일정" + "1")
                .description("무슨 일정? 일정" + "1")
                .schedulesStatus(ScheduleStatus.L_RECOMMEND)
                .meetingType(MeetingType.ONLINE)
                .members(List.of(CreateScheduleMembersDto.builder().memberId("1a").build()))
                .build();

            // when
            scheduleCommandService.createSchedule(request, memberId);

            // then

            List<Schedule> schedules = scheduleQueryRepository.findAll();
            assertEquals(1, schedules.size());
            assertEquals("일정1", schedules.get(0).getScheduleName());

        }
    }


}
