package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.Vote;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VoteQueryRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT v from Vote v where v.")
    List<Vote> findByScheduleId(Long scheduleId);
}
