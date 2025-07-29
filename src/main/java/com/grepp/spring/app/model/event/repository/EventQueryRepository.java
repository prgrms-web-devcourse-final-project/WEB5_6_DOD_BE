package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.model.event.dto.AllTimeEventDto;
import com.grepp.spring.app.model.event.entity.Event;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventQueryRepository extends JpaRepository<Event, Long> {

    ArrayList<Event> findByGroupId(Long groupId);

    AllTimeEventDto findAllTimeEventById(Long eventId);
}
