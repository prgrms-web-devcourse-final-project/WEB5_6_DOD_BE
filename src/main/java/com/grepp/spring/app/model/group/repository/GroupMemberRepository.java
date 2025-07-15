package com.grepp.spring.app.model.group.repository;

import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByMemberId(String memberId);
    Optional<GroupMember> findByGroupIdAndMemberId(Long groupId, String memberId);

    // 나중에 적절한 Repository 로 분리 예정
    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.member = :member AND gm.groupAdmin = true")
    List<Group> findGroupsByMemberAndAdmin(@Param("member") Member member);

    // 특정 멤버 제외 그룹 내 모든 리더 조회
    @Query("SELECT gm FROM GroupMember gm WHERE gm.group = :group AND gm.member != :member AND gm.role = 'GROUP_LEADER'")
    List<GroupMember> findByGroupAndLeaderAndMemberNot(@Param("group") Group group, @Param("member") Member member);

    // 그룹의 모든 멤버 조회
    List<GroupMember> findByGroup(Group group);

    // 특정 멤버를 GroupMember 에서 삭제
    void deleteByMember(Member member);
}
