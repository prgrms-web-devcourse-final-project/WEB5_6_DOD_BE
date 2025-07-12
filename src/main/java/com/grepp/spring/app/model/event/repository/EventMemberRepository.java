package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.model.event.entity.EventMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventMemberRepository extends JpaRepository<EventMember, Long> {

    Long countByEventId(Long eventId);

    Boolean existsByEventIdAndMemberId(Long eventId, String memberId);

    Optional<EventMember> findByEventIdAndMemberIdAndActivatedTrue(Long eventId, String memberId);

}
