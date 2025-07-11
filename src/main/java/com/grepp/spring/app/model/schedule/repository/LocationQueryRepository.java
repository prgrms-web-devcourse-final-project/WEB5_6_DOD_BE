package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationQueryRepository extends JpaRepository<Location, Long> {

}
