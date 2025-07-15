package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.entity.MetroTransfer;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleQueryRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByEventIdAndStatusInAndActivatedTrue(Long eventId, List<ScheduleStatus> list);

    @Query("SELECT mt from MetroTransfer mt where mt.location.id = :locationId")
    List<MetroTransfer> findByLocationId(@Param("locationId") Long locationId);

}
