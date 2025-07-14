package com.grepp.spring.app.model.schedule.repository;

import com.grepp.spring.app.model.schedule.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceCommandRepository extends JpaRepository<Workspace, Long> {

}
