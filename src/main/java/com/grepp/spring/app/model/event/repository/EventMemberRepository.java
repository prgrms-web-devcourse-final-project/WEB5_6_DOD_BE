package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.model.event.entity.EventMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventMemberRepository extends JpaRepository<EventMember, Long> {

}
