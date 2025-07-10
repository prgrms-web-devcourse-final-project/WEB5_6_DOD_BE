package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

// 나중에 Event 패키지의 레포로 변경 후 삭제
public interface EventRepository extends JpaRepository<Event, Long> {

}
