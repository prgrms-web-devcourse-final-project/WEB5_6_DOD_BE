package com.grepp.spring.app.model.event.repository;

import com.grepp.spring.app.model.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByGroupIdAndMemberId(Long groupId, String memberId);

}
