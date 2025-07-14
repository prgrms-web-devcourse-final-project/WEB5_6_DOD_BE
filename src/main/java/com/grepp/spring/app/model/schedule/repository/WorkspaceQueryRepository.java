package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.Workspace;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceQueryRepository extends JpaRepository<Workspace, Long> {

    List<Workspace> findAllByScheduleId(Long scheduleId);

}
