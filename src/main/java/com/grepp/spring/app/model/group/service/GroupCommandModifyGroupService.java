package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.request.ModifyGroupInfoRequest;
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
public class GroupCommandModifyGroupService {

    private final GroupCommandRepository groupCommandRepository;
    private final GroupMemberCommandRepository groupMemberCommandRepository;
    private final GroupQueryRepository groupQueryRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final MemberRepository memberRepository;

    // 그룹 정보 수정
    @Transactional
    public void modifyGroup(Long groupId, ModifyGroupInfoRequest request){
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
        Group group1 =  group.get();

        Optional<GroupMember> groupMember = groupMemberQueryRepository.findByGroupIdAndMemberId(groupId, member.getId());
        // 예외 발생: 해당 group의 그룹원이 아닌 경우 - USER_NOT_IN_GROUP
        if(groupMember.isEmpty()){
            throw new UserNotInGroupException(GroupErrorCode.USER_NOT_IN_GROUP);
        }

        // 예외 발생: 해당 group의 그룹장이 아닌 경우 - NOT_GROUP_LEADER
        if(!groupMember.get().getRole().equals(GroupRole.GROUP_LEADER)){
            throw new NotGroupLeaderException(GroupErrorCode.NOT_GROUP_LEADER);
        }

        // patch 메서드 진행
        if(!request.getGroupName().isEmpty()){
            group1.setName(request.getGroupName());
        }
        if(!request.getDescription().isEmpty()){
            group1.setDescription(request.getDescription());
        }
        groupCommandRepository.save(group1);
    }
}
