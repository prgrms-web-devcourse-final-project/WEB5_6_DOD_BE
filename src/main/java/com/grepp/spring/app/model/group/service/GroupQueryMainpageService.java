package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupResponse;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.group.dto.GroupDetailDto;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberQueryRepository;
import com.grepp.spring.app.model.group.repository.GroupRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupQueryMainpageService {

  private final MemberRepository memberRepository;
  private final GroupMemberQueryRepository groupMemberQueryRepository;

  // 그룹 조회
  public ShowGroupResponse displayGroup() {
    // http 요청 사용자 조회
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Principal user = (Principal) authentication.getPrincipal();
    Member member = memberRepository.findById(user.getUsername()).orElseThrow();
    // TODO: member가 없다면 throw 예외(회원이 아닙니다.)

    List<GroupMember> groupMembers = groupMemberQueryRepository.findGroupedByMember(member);
    List<Group> myGroups = groupMembers.stream()
        .map(GroupMember::getGroup)
        .toList();

    // 내가 속한 모든 그룹들의 멤버를 한 번에 조회?
    List<Long> groupIds = myGroups.stream().map(Group::getId).toList();
    List<GroupMember> allGroupMembers = groupMemberQueryRepository.findByGroupIdIn(groupIds);

    // DTO 변환 (멤버 수 + 그룹장 이미지 자동 세팅)
    List<GroupDetailDto> groups = myGroups.stream()
        .map(group -> GroupDetailDto.from(group, allGroupMembers))
        .toList();

    return new ShowGroupResponse(groups);
  }

}
