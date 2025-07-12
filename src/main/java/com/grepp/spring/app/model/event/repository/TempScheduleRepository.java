package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.model.event.entity.TempSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TempScheduleRepository extends JpaRepository<TempSchedule, Long> {

    Optional<TempSchedule> findByEventMemberIdAndDateAndActivatedTrue(Long eventMemberId, LocalDate date);

}