package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberQueryRepository;
import com.grepp.spring.app.model.group.repository.GroupQueryRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.infra.error.exceptions.group.GroupNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.NotGroupLeaderException;
import com.grepp.spring.infra.error.exceptions.group.NotGroupUserException;
import com.grepp.spring.infra.response.GroupErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupCommandDeleteGroupService {

    private final GroupCommandRepository groupCommandRepository;
    private final GroupMemberCommandRepository groupMemberCommandRepository;
    private final GroupQueryRepository groupQueryRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final MemberRepository memberRepository;

    // 그룹 삭제
    public void deleteGroup(Long groupId){
        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다: 401)

        Optional<Group> groupOptional = groupQueryRepository.findById(groupId);
        // 예외 발생: 해당 group은 존재하지 않음 - 404 GROUP_NOT_FOUND
        if(groupOptional.isEmpty()){
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }

        Optional<GroupMember> groupMemberOptional = groupMemberQueryRepository.findByGroupIdAndMemberId(groupId,
            member.getId());
        // 예외 발생: http 메서드를 요청한 유저가 해당 그룹의 그룹원이 아님 - 403 NOT_GROUP_MEMBER
        if(groupMemberOptional.isEmpty()){
            throw new NotGroupUserException(GroupErrorCode.NOT_GROUP_MEMBER);
        }
        GroupMember groupMember = groupMemberOptional.get();

        // 예외 발생: http 메서드를 요청한 유저가 해당 그룹의 그룹장이 아님 - 403 NOT_GROUP_LEADER
        if(!groupMember.getRole().equals(GroupRole.GROUP_LEADER)){
            throw new NotGroupLeaderException(GroupErrorCode.NOT_GROUP_LEADER);
        }

        // 그룹 삭제 진행
        // cascade 삭제
        groupCommandRepository.deleteById(groupId);
    }
}
