package com.grepp.spring.app.model.event;


import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.service.EventCommandService;
import com.grepp.spring.infra.error.exceptions.event.AlreadyJoinedEventException;
import com.grepp.spring.infra.error.exceptions.event.InvalidEventDataException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class EventJoinServiceTest {

    @Autowired
    private EventCommandService eventCommandService;

    @Autowired
    private EventMemberRepository eventMemberRepository;


    @BeforeEach
    void setUp() {
        Long eventId = 2L;
        Long groupId = 1L;
        String currentMemberId = "Exception1";
        eventCommandService.joinEvent(eventId, groupId, currentMemberId);
        eventId = 3L;
        eventCommandService.joinEvent(eventId, groupId, currentMemberId);
    }

    @AfterEach
    void restore() {
        eventMemberRepository.deleteAllInBatch();
    }


    @Nested
    @DisplayName("이벤트 참여 (joinEvent) 테스트")
    class JoinEventTests {

        @Test
        @DisplayName("성공 해야 함: 멀티스레드 100명이 동시에 이벤트에 참여한다")
        void joinEvent_MultiThread() throws InterruptedException {
            int threadCount = 100;
            Long eventId = 1L;
            Long groupId = 1L;

            // 스레드 풀과 동기화용 래치
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            IntStream.rangeClosed(1, threadCount).forEach(i -> {
                executor.submit(() -> {
                    try {
                        String currentMemberId = i + "a"; // "1a", "2a", ..., "100a"
                        eventCommandService.joinEvent(eventId, groupId, currentMemberId);
                    } catch (Exception e) {
                        // 예외는 무시 (정원 초과 등)
                        System.out.println("Thread " + i + " failed: " + e.getClass().getSimpleName());
                    } finally {
                        latch.countDown();
                    }
                });
            });

            // 모든 쓰레드 종료 대기
            latch.await();
            executor.shutdown();

            // then: 실제로 저장된 수는 이벤트 정원 이하
            List<EventMember> joined = eventMemberRepository.findByEventId(eventId);
            System.out.println("최종 참여 인원: " + joined.size());

            // capacity가 10이라고 가정한다면
            assertTrue(joined.size() <= 10, "정원 초과 참여가 발생했습니다.");

        }


        @Test
        @DisplayName("성공: 사용자가 이벤트에 정상적으로 참여한다")
        void joinEvent_Success() {

            // given
            Long eventId = 1L;
            Long groupId = 1L;
            String currentMemberId = "Exception1";
            // when
            eventCommandService.joinEvent(eventId, groupId, currentMemberId);
            // then
            List<EventMember> members = eventMemberRepository.findByEventId(eventId);
            assertEquals(1, members.size());
            assertEquals(currentMemberId, members.get(0).getMember().getId());
        }

        @Test
        @DisplayName("실패: 이미 참여한 이벤트에 중복 참여할 수 없다")
        void joinEvent_Fail_AlreadyJoined() {
            // given
            Long eventId = 3L;
            Long groupId = 1L;
            String currentMemberId = "Exception1";

            // when
            assertThrows(AlreadyJoinedEventException.class, () -> {
                eventCommandService.joinEvent(eventId, groupId, currentMemberId);
            });
        }

        @Test
        @DisplayName("실패: 이벤트 정원이 가득 차면 참여할 수 없다")
        void joinEvent_Fail_CapacityExceeded() {
            // given
            Long eventId = 3L;
            Long groupId = 1L;
            String currentMemberId = "1a";

            // when
            assertThrows(InvalidEventDataException.class, () -> {
                eventCommandService.joinEvent(eventId, groupId, currentMemberId);
            });
        }
    }

}
