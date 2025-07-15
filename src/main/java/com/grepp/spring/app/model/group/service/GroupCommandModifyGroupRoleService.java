package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.request.ControlGroupRoleRequest;
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
import com.grepp.spring.infra.error.exceptions.group.UserNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.UserNotInGroupException;
import com.grepp.spring.infra.response.GroupErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupCommandModifyGroupRoleService {

    private final GroupCommandRepository groupCommandRepository;
    private final GroupMemberCommandRepository groupMemberCommandRepository;
    private final GroupQueryRepository groupQueryRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;

    // 그룹 멤버 권한 수정
    @Transactional
    public void modifyGroupRole(Long groupId, ControlGroupRoleRequest request){
        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다.)

        Optional<Group> group = groupQueryRepository.findById(groupId);
        // 예외 발생: 해당 group은 존재하지 않음 - 404 GROUP_NOT_FOUND
        if(group.isEmpty()){
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }

        // 예외 발생: 해당 group에 member가 존재하지 않음 - 404 USER_NOT_IN_GROUP
        // http 메서드 요청한 member
        Optional<GroupMember> groupMember = groupMemberQueryRepository.findByGroupIdAndMemberId(groupId, member.getId());
        if(groupMember.isEmpty()){
            throw new UserNotInGroupException(GroupErrorCode.USER_NOT_IN_GROUP);
        }

        GroupRole groupRole = groupMember.get().getRole();
        // 예외 발생: member의 그룹 권한이 GroupLeader가 아닌 경우 - 403 NOT_GROUP_LEADER
        if(!groupRole.equals(GroupRole.GROUP_LEADER)){
            throw new NotGroupLeaderException(GroupErrorCode.NOT_GROUP_LEADER);
        }

        // 예외 발생: request의 userId가 db에 없는 유저 - 404 USER_NOT_FOUND
        Optional<Member> memberOptional = memberRepository.findById(request.getUserId());
        if(memberOptional.isEmpty()){
            throw new UserNotFoundException(GroupErrorCode.USER_NOT_FOUND);
        }

        Optional<GroupMember> groupMemberOptional = groupMemberQueryRepository.findByGroupIdAndMemberId(groupId,
            request.getUserId());
        // 예외 발생: request의 userId가 group에 없는 유저 - 404 USER_NOT_IN_GROUP
        if(groupMemberOptional.isEmpty()){
            throw new UserNotInGroupException(GroupErrorCode.USER_NOT_IN_GROUP);
        }

        // 권한 최신화
        // 만약 슈퍼방장이 강등된다면, 강등시킨 방장에게 슈퍼방장을 주자.
        GroupMember groupMember1 = groupMemberOptional.get();
        groupMember1.setRole(request.getGroupRole());
        if(request.getGroupRole().equals(GroupRole.GROUP_MEMBER) && groupMember1.getGroupAdmin()){
            groupMember1.setGroupAdmin(false);
            groupMember.get().setGroupAdmin(true);
        }
        groupMemberCommandRepository.save(groupMember.get());
        groupMemberCommandRepository.save(groupMember1);
    }
}
