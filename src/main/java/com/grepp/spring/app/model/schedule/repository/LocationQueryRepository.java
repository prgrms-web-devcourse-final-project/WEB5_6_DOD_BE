package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.Location;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationQueryRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l where l.schedule.id = :scheduleId")
    List<Location> findByScheduleId(@Param("scheduleId") Long scheduleId);
}
