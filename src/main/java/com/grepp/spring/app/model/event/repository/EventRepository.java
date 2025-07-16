package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.model.event.entity.Event;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {


    ArrayList<Event> findByGroupId(Long groupId);
}
