package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.Metro;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetroQueryRepository extends JpaRepository<Metro, Long> {

    Optional<Metro> findByName(String name);
}
