package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.model.event.entity.CandidateDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateDateRepository extends JpaRepository<CandidateDate, Long> {

}
