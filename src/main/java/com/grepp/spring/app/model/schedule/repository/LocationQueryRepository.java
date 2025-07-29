package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.Location;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationQueryRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l where l.schedule.id = :scheduleId")
    List<Location> findByScheduleId(@Param("scheduleId") Long scheduleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Location l where l.id = :id")
    Optional<Location> findByIdWithPessimisticLock(@Param("id") Long id);

}
