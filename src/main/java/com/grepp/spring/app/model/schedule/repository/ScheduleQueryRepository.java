package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleQueryRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByEventIdAndStatusInAndActivatedTrue(Long eventId, List<ScheduleStatus> list);

}
