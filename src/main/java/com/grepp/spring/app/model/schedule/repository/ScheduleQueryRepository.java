package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleQueryRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByEventIdAndStatusInAndActivatedTrue(Long eventId, List<ScheduleStatus> list);


    List<Schedule> findByEvent(Event event);
}
