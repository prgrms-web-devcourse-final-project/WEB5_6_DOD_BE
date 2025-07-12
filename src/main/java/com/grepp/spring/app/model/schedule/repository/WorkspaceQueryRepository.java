package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.Workspace;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WorkspaceQueryRepository extends JpaRepository<Workspace, Long> {

    List<Workspace> findAllByScheduleId(Long scheduleId);

    @Query("select w from Workspace w where w.schedule.id = :scheduleId and w.id = :workspaceId")
    Workspace findworkspace(@Param("scheduleId") Long scheduleId, @Param("workspaceId") Long workspaceId);
}
