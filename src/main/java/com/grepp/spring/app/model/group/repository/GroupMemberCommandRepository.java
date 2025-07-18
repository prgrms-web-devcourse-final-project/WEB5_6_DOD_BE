package com.grepp.spring.app.model.group.repository;

import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberCommandRepository extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByGroup(Group group);

    void deleteByGroupAndMemberId(Group group, String id);

    ArrayList<GroupMember> findByGroupId(Long groupId);

    Optional<GroupMember> findByGroupIdAndMemberId(Long groupId, String memberId);

    ArrayList<GroupMember> findByGroupAndRole(Group group, GroupRole role);
}
