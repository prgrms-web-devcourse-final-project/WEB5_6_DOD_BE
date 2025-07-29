package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.entity.EventMember;
import jakarta.persistence.LockModeType;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;

public interface EventMemberRepository extends JpaRepository<EventMember, Long> {

    Long countByEventId(Long eventId);

    Boolean existsByEventIdAndMemberId(Long eventId, String memberId);

    Optional<EventMember> findByEventIdAndMemberIdAndActivatedTrue(Long eventId, String memberId);

    List<EventMember> findAllByEventIdAndActivatedTrue(Long eventId);

    ArrayList<EventMember> findByEvent(Event event);

    void deleteByEventAndMemberId(Event event, String id);

    List<EventMember> findByEventId(Long eventId);
}
