package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.Vote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteQueryRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT v from Vote v where v.schedule.id = :scheduleId")
    List<Vote> findByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("select v from Vote v where v.scheduleMember.id = :scheduleMemberId")
    Vote findByScheduleMemberId(@Param("scheduleMemberId") Long scheduleMemberId);

    @Query("select v.location.id from Vote v where v.schedule.id = :id")
    List<Long> findByScheduleIdAndMemberId(@Param("id") Long id);
}
