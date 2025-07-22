package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.MetroTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MetroTransferCommandRepository extends JpaRepository<MetroTransfer, Long> {

    @Modifying
    @Query("delete from MetroTransfer mt where mt.schedule.id = :scheduleId and mt.isMemberSuggested = false")
    void deleteByScheduleId(@Param("scheduleId") Long scheduleId);
}
