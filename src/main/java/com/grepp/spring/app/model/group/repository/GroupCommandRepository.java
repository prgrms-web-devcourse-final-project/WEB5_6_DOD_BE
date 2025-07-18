package com.grepp.spring.app.model.group.repository;

import com.grepp.spring.app.model.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupCommandRepository extends JpaRepository<Group, Long> {

}
