package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupMemberResponse;
import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupResponse;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.dto.GroupDetailDto;
import com.grepp.spring.app.model.group.dto.GroupUser;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberQueryRepository;
import com.grepp.spring.app.model.group.repository.GroupQueryRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.infra.error.exceptions.GroupNotFoundException;
import com.grepp.spring.infra.error.exceptions.NotGroupUserException;
import com.grepp.spring.infra.response.GroupErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupQueryService {
    private final GroupQueryRepository groupQueryRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final MemberRepository memberRepository;

    // 그룹 조회
    public ShowGroupResponse displayGroup(){
        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다.)

        List<GroupMember> groupMembers = groupMemberQueryRepository.findByMember(member);
        // TODO: groupMembers.length()가 0이면 throw 예외(속한 그룹이 없습니다.)

        // 일단 구현은 뭐 어케 하긴 했는데, QueryDSL을 나중에 꼭 도입하자.. 정신건강에 너무너무 해롭다.
        List<GroupDetailDto> groups = groupMembers.stream()
            .map(gm -> new GroupDetailDto(
                gm.getGroup().getId(),
                gm.getGroup().getName(),
                gm.getGroup().getDescription(),
                0 // 일단 0으로 초기화
            ))
            .toList();

        // groupId들 추출
        List<Long> groupIds = groups.stream()
            .map(GroupDetailDto::getGroupId)
            .toList();

        // 추출한 groupId들을 가지고 있는 GroupMember들 추출
        List<GroupMember> all = groupMemberQueryRepository.findByGroupIdIn(groupIds);

        // 각 groupId가 나올 때마다 해당 groupId의 갯수 카운팅(+1)
        Map<Long, Long> countMap = all.stream()
            .collect(Collectors.groupingBy(
                gm -> gm.getGroup().getId(),
                Collectors.counting()
            ));

        // 0으로 초기화 했던 groups에 counting한 수 최신화
        groups.forEach(dto ->
            dto.setGroupMemberNum(
                countMap.getOrDefault(dto.getGroupId(), 0L).intValue()
            )
        );

        return new ShowGroupResponse(groups);
    }


    // 그룹 멤버 조회
    public ShowGroupMemberResponse displayGroupMember(Long groupId){
        Optional<Group> group = groupQueryRepository.findById(groupId);
        // 예외 발생: 해당 group은 존재하지 않음 - 404 GROUP_NOT_FOUND
        if(group.isEmpty()){
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }

        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다.)

        // checking이 false 그대로면, group에 속하지 않은 멤버가 group에 있는 멤버를 조회하는 메서드 요청(예외처리 함)
        boolean checking = false;
        ShowGroupMemberResponse response = new ShowGroupMemberResponse();
        List<GroupMember> groupMembers = groupMemberQueryRepository.findByGroup(group.get());

        ArrayList<GroupUser> groupUsers = response.getGroupUser();
        for(GroupMember groupMember: groupMembers){

            String memberId = groupMember.getMember().getId();
            String memberName = memberRepository.findById(memberId).get().getName();
            GroupRole groupRole = groupMember.getRole();
            groupUsers.add(new GroupUser(memberId, memberName, groupRole));
            if(memberId.equals(member.getId())) {
                checking=true;
            }
        }

        // 예외 발생: http요청을 한 member가 속하지 않은 groupId를 탐색하려 하는 경우 - NOT GROUP MEMBER
        if(!checking){
            throw new NotGroupUserException(GroupErrorCode.NOT_GROUP_MEMBER);
        }

        return response;
    }

}
