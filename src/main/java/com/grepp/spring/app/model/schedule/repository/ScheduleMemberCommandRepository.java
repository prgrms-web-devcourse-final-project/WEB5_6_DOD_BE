package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleMemberCommandRepository extends JpaRepository<ScheduleMember, Long> {

    @Modifying
    @Query("DELETE from ScheduleMember sm where sm.schedule.id = :scheduleId")
    void deleteAllByScheduleId(@Param("scheduleId") Long scheduleId);

    void deleteByScheduleAndMemberId(Schedule schedule, String id);

    List<ScheduleMember> findByScheduleId(Long scheduleId);

    ArrayList<ScheduleMember> findBySchedule(Schedule schedule);

    ScheduleMember findByScheduleAndMemberId(Schedule schedule, String id);
}
