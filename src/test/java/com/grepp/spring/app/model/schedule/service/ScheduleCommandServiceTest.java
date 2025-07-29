package com.grepp.spring.app.model.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.grepp.spring.app.model.schedule.code.VoteStatus;
import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.LocationQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.app.model.schedule.repository.VoteQueryRepository;
import com.grepp.spring.infra.error.exceptions.schedule.LocationNotFoundException;
import com.grepp.spring.infra.response.ScheduleErrorCode;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class ScheduleCommandServiceTest {

    @Autowired
    private ScheduleCommandService scheduleCommandService;

    @Autowired
    private LocationQueryRepository locationRepository;

    @Autowired
    private ScheduleCommandRepository scheduleCommandRepository;

    private Schedule testSchedule;
    private Location testLocation1;
    private Location testLocation2;
    @Autowired
    private ScheduleMemberRepository scheduleMemberRepository;

    @Autowired
    private VoteQueryRepository voteQueryRepository;

    @BeforeEach
    @Transactional
    void setUp(){
        // 기존의 데이터 정리
        scheduleCommandRepository.deleteAll();
        scheduleMemberRepository.deleteAll();
        locationRepository.deleteAll();

        // 테스트를 위한 Schedule 생성
        testSchedule = new Schedule();
        testSchedule.setScheduleName("테스트 스케줄");
        testSchedule = scheduleCommandRepository.save(testSchedule);

        // 테스트를 위한 Location 생성
        testLocation1 = new Location();
        testLocation1.setSchedule(testSchedule);
        testLocation1.setLatitude(37.55315);
        testLocation1.setLongitude(126.972533);
        testLocation1.setName("테스트 장소 1");
        testLocation1.setVoteCount(0);
        testLocation1.setStatus(VoteStatus.DEFAULT);
        testLocation1 = locationRepository.save(testLocation1);

        testLocation2 = new Location();
        testLocation2.setSchedule(testSchedule);
        testLocation2.setLatitude(37.55315);
        testLocation2.setLongitude(126.972533);
        testLocation2.setName("테스트 장소 1");
        testLocation2.setVoteCount(0);
        testLocation2.setStatus(VoteStatus.DEFAULT);
        testLocation2 = locationRepository.save(testLocation2);

        // 테스트를 위한 ScheduleMember 생성
        for (int i = 0; i < 100; i++) {
            ScheduleMember member = new ScheduleMember();
            member.setSchedule(testSchedule);
            member.setName("참여자" + i);
            scheduleMemberRepository.save(member);
        }
    }

    @Test
    @DisplayName("10명이 동시에 투표 진행 시 득표율이 정확히 증가하는지에 대한 테스트")
    void voteMiddleLocation() throws InterruptedException {
        final int threads = 100; // 동시에 투표할 사용자 수
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threads);

        final Long locationId = testLocation1.getId();
        assertThat(locationId).isNotNull();

        System.out.println("테스트 시작: " + locationId + "번 장소에 " + threads + "명 동시 투표");
        System.out.println("초기 voteCnt: " + testLocation1.getVoteCount());
        // 모든 스케줄 멤버를 각 스레스에 할당
        List<ScheduleMember> members = scheduleMemberRepository.findAllBySchedule(testSchedule);

        if (members.size() < threads) {
            fail("테스트에 필요한 일정의 멤버 수가 부족합니다.");
        }


        for (int i = 0; i < threads; i++) {
            final int memberIndex = i;
            final ScheduleMember currentMember = members.get(memberIndex);

            executorService.submit(() -> {
                try {
                    startLatch.await(); // 모든 스레드가 여기서 대기 후 동시에 시작

                    // 투표 메서드 호출 -> 아마 여기서 문제가 생길 듯?
                    scheduleCommandService.voteMiddleLocation(testSchedule, currentMember, testLocation1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println(Thread.currentThread().getName() + " -인터럽트 발생: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println(Thread.currentThread().getName() + "- 오류 발생" + e.getMessage());
                } finally {
                    endLatch.countDown(); // 스레드 완료 시점
                }
            });
        }

        startLatch.countDown(); // 모든 스레드가 동시에 시작
        executorService.shutdown(); // 스레드 풀 셧다운

        // 모든 스레드가 작업을 완료할 때까지 최대 60초 대기
        boolean finished = endLatch.await(60, TimeUnit.SECONDS);
        // 모든 스레드가 시간 내에 종료되었는지 체크
        assertTrue(finished);

        // 최종 데이터 검증
        // 트랜젝션 종료 후 DB에 반영된 값을 다시 조회

        Location finalLocation = locationRepository.findById(testLocation1.getId())
            .orElseThrow(() -> new LocationNotFoundException(ScheduleErrorCode.LOCATION_NOT_FOUND));

        System.out.println("기대하는 최종 득표율: " + threads);
        System.out.println("실제 DB에 반영된 득표율: " + finalLocation.getVoteCount());

        // 득표율이 예상 값보다 작거나 다른지 확인
        assertThat(finalLocation.getVoteCount()).isLessThan(threads);

    }

    @Test
    @DisplayName("비관적 락 환경에서 서로 다른 장소 동시 투표 시 데드락 발생 여부 테스트")
    void deadlockOnMultipleLocation() throws InterruptedException {
        final int threads = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threads);

        // 총 투표 수
        int expectedTotalVotes = threads;

        List<ScheduleMember> members = scheduleMemberRepository.findAllBySchedule(testSchedule);
        if (members.size() < threads) {
            fail("테스트에 필요한 일정의 멤버 수가 부족합니다.");
        }

        for (int i = 0; i < threads; i++) {
            final int memberIndex = i;
            final ScheduleMember currentMember = members.get(memberIndex);

            final Location targetLocation = (i % 2 == 0) ? testLocation1 : testLocation2;

            executorService.submit(() -> {
                try {
                    startLatch.await();

                    scheduleCommandService.voteMiddleLocation(testSchedule, currentMember, targetLocation);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println(Thread.currentThread().getName() + " - 인터럽트 발생: " + e.getMessage());
                } catch (Exception e) {
                    // 데드락이나 다른 SQL 예외가 발생할 것으로 예상
                    System.err.println(Thread.currentThread().getName() + " - 오류 발생: " + e.getMessage());
                    e.printStackTrace(); // 스택 트레이스 출력하여 데드락 확인
                } finally {
                    endLatch.countDown(); // 스레드 완료 시점
                }
            });
        }

        startLatch.countDown(); // 모든 스레드가 동시에 시작
        executorService.shutdown(); // 스레드 풀 셧다운

        // 모든 스레드가 작업을 완료할 때까지 최대 60초 대기
        boolean finished = endLatch.await(60, TimeUnit.SECONDS);
        // 모든 스레드가 시간 내에 종료되었는지 체크
        assertTrue(finished);

        Location finalLocation1 = locationRepository.findById(testLocation1.getId())
            .orElseThrow(() -> new IllegalArgumentException("장소 A를 찾을 수 없습니다."));
        Location finalLocation2 = locationRepository.findById(testLocation2.getId())
            .orElseThrow(() -> new IllegalArgumentException("장소 B를 찾을 수 없습니다."));

        // 모든 투표 기록의 총 개수 확인 (voteCommandRepository에 의해 저장된 레코드 수)
        // DTO에 locationId를 포함하도록 변경했으니, voteRepository에서 scheduleId로 전체 투표 기록을 조회
        long totalVotesInDb = voteQueryRepository.findByScheduleId(testSchedule.getId()).size();

        System.out.println("기대하는 총 투표 수: " + expectedTotalVotes);
        System.out.println("실제 DB에 반영된 장소 1 투표수: " + finalLocation1.getVoteCount());
        System.out.println("실제 DB에 반영된 장소 2 투표수: " + finalLocation2.getVoteCount());
        System.out.println("실제 DB에 저장된 총 투표 기록 수: " + totalVotesInDb);
        System.out.println("최종 모든 Location의 총 득표율: " + (finalLocation1.getVoteCount() + finalLocation2.getVoteCount()));

        // 검증: 총 투표 기록이 예상보다 적거나, 투표 카운트 합이 예상보다 적을 경우 데드락 문제 증명
        assertThat(totalVotesInDb).isLessThan(expectedTotalVotes); // 모든 Vote 기록이 저장되지 않았을 수 있음
        assertThat(finalLocation1.getVoteCount() + finalLocation2.getVoteCount()).isLessThan(expectedTotalVotes); // 득표율 합계가 기대보다 작음
    }





}