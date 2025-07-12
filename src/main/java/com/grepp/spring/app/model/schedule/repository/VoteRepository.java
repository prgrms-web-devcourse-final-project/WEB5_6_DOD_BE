package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

}
