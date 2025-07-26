package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.entity.TempSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TempScheduleRepository extends JpaRepository<TempSchedule, Long> {

    Optional<TempSchedule> findByEventMemberIdAndDateAndActivatedTrue(Long eventMemberId, LocalDate date);

    List<TempSchedule> findAllByEventMemberInAndActivatedTrueOrderByEventMemberIdAscDateAsc(List<EventMember> eventMembers);

    List<TempSchedule> findAllByEventMemberIdAndActivatedTrue(Long eventMemberId);

    boolean existsByEventMemberInAndModifiedAtAfter(List<EventMember> eventMembers, LocalDateTime after);
}