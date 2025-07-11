package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleMemberQueryRepository extends JpaRepository<ScheduleMember, Long> {

    List<ScheduleMember> findByScheduleId(Long scheduleId);
}
