package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleMemberRepository extends JpaRepository<ScheduleMember, Long> {

    // 특정 멤버가 속하는 일정 리스트 조회
    @Query("SELECT sm.schedule FROM ScheduleMember sm WHERE sm.member = :member")
    List<Schedule> findByMember(@Param("member") Member member);

    // 특정 일정에 참여하는 멤버 조회
    @Query("SELECT sm FROM ScheduleMember sm WHERE sm.schedule = :schedule AND sm.member != :member")
    List<ScheduleMember> findByScheduleAndMemberNot(@Param("schedule") Schedule schedule, @Param("member") Member member);

    // 특정 멤버를 ScheduleMember 에서 삭제
    void deleteByMember(Member member);

    // 특정 일정에 특정 멤버가 참여 중인지 확인 + 그 멤버의 참여 상태 가져오기
    Optional<ScheduleMember> findByScheduleIdAndMemberId(Long scheduleId, String memberId);

    // 일정에 참여하는 멤버 조회 (나 포함)
    List<ScheduleMember> findAllBySchedule(Schedule schedule);

}
