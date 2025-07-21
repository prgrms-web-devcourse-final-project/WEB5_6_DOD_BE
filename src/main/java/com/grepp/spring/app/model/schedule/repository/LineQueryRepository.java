package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.Line;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LineQueryRepository extends JpaRepository<Line, Long> {

    @Query("select l from Line l where l.metro.id = :id")
    List<Line> findByMetroId(@Param("id") Long id);
}
