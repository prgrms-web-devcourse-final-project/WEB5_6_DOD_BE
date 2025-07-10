package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.model.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findById(Long id);

}
