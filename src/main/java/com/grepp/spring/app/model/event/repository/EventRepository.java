package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.model.event.entity.Event;
import jakarta.persistence.LockModeType;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {


    ArrayList<Event> findByGroupId(Long groupId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :eventId")
    Optional<Event> findEventForUpdate(@Param("eventId") Long eventId);
}
