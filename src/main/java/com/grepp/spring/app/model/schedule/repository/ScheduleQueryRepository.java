package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.MetroTransfer;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleQueryRepository extends JpaRepository<Schedule, Long> {


    @Query("SELECT m from MetroTransfer m where m.location.id = :locationId")
    List<MetroTransfer> findByLocationId(@Param("locationId") Long locationId);
}
