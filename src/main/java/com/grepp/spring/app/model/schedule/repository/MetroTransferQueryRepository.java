package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.MetroTransfer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MetroTransferQueryRepository extends JpaRepository<MetroTransfer, Long> {

    @Query("SELECT mt from MetroTransfer mt where mt.location.id = :locationId")
    List<MetroTransfer> findByLocationId(@Param("locationId") Long locationId);

}
