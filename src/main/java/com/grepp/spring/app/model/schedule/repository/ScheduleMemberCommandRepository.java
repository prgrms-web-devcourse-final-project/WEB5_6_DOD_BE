package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleMemberCommandRepository extends JpaRepository<ScheduleMember, Long> {

}
