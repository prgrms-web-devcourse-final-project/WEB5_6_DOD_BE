package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ScheduleQueryRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByEventIdAndStatusInAndActivatedTrue(Long eventId, List<ScheduleStatus> list);


    List<Schedule> findByEvent(Event event);

    Event findEventById(Long id);

    @Query("SELECT MAX(s.createdAt) FROM Schedule s WHERE s.event.id = :eventId AND s.status IN ('L_RECOMMEND', 'E_RECOMMEND') AND s.activated = true")
    Optional<LocalDateTime> findMaxCreatedAtByEventIdAndStatusIn(@Param("eventId") Long eventId);
}
