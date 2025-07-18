package com.grepp.spring.app.model.group.repository;

import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.member.entity.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupMemberQueryRepository extends JpaRepository<GroupMember, Long> {

    // entityGraph를 통해 lazy로 지연로딩 되었던 연관관계를, 이 메서드에서만 eager로 동작하도록 설정
    @EntityGraph(attributePaths = {"group"})
    List<GroupMember> findByMember(Member member);
    Optional<GroupMember> findByGroupIdAndMemberId(Long groupId, String memberId);

    List<GroupMember> findByMemberId(String memberId);

    Long countByGroup(Group group);

    List<GroupMember> findByGroupIdIn(List<Long> groupIds);

    List<GroupMember> findByGroup(Group group);

    ArrayList<GroupMember> findByGroupAndRole(Group group, GroupRole role);

    ArrayList<GroupMember> findByGroupAndMember(Group group, Member member);

    ArrayList<GroupMember> findByGroupId(Long groupId);

    @Query("""
    SELECT gm
    FROM GroupMember gm
    JOIN FETCH gm.group g
    WHERE gm.member = :member
      AND g.isGrouped = true
""")
    List<GroupMember> findGroupedByMember(@Param("member") Member member);
}
