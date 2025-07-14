package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleMemberQueryRepository extends JpaRepository<ScheduleMember, Long> {

    List<ScheduleMember> findByScheduleId(Long scheduleId);

//    @Query("select sm from ScheduleMember sm where sm.member.id = :memberId and sm.schedule.id = :scheduleId")
//    ScheduleMember findByMemberId(@Param("memberId") String memberId, @Param("scheduleId") Long scheduleId);

    //임시
    @Query("select sm from ScheduleMember sm where sm.member.id = :memberId and sm.schedule.id = :scheduleId")
    ScheduleMember findScheduleMember(@Param("memberId") String memberId, @Param("scheduleId") Long scheduleId);

}
