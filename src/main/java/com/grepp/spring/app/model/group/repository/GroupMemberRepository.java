package com.grepp.spring.app.model.group.repository;

import com.grepp.spring.app.model.group.entity.GroupMember;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByMemberId(String memberId);
    Optional<GroupMember> findByGroupIdAndMemberId(Long groupId, String memberId);
}
