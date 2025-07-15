package com.grepp.spring.app.model.group.repository;

import com.grepp.spring.app.model.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberCommandRepository extends JpaRepository<GroupMember, Long> {

    void deleteByGroupId(Long groupId);
}
