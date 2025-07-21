package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.dto.CreateDepartLocationDto;
import com.grepp.spring.app.model.schedule.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationCommandRepository extends JpaRepository<Location,Long> {

//    @Modifying
//    @Query("delete from Location l where l.schedule.id = :scheduleId")
//    void deleteByScheduleId(@Param("scheduleId") Long scheduleId);

    @Modifying
    @Query("delete from Location l where l.schedule.id = :scheduleId and l.suggestedMemberId is null")
    void deleteLocation(@Param("scheduleId") Long scheduleId);
}
