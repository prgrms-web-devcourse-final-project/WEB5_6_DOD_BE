package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.model.event.dto.AllTimeEventMemberDto;
import com.grepp.spring.app.model.event.entity.EventMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventMemberQueryRepository extends JpaRepository<EventMember, Long> {

    List<AllTimeEventMemberDto> findAllTimeEventMembersByEventId(Long eventId);

}
