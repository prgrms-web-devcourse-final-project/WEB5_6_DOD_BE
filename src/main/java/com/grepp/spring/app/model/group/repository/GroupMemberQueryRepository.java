package com.grepp.spring.app.model.group.repository;

import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberQueryRepository extends JpaRepository<GroupMember, Long> {

    // entityGraph를 통해 lazy로 지연로딩 되었던 연관관계를, 이 메서드에서만 eager로 동작하도록 설정
    @EntityGraph(attributePaths = {"group"})
    List<GroupMember> findByMember(Member member);
    Optional<GroupMember> findByGroupIdAndMemberId(Long groupId, String memberId);

    Long countByGroup(Group group);

    List<GroupMember> findByGroup_IdIn(List<Long> groupIds);
}
