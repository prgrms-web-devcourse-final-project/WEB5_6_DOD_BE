package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.MetroTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetroTransferQueryRepository extends JpaRepository<MetroTransfer, Long> {

}
