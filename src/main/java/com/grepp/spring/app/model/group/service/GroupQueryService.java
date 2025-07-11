package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupResponse;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.group.dto.GroupDetailDto;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberQueryRepository;
import com.grepp.spring.app.model.group.repository.GroupQueryRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import java.util.List;
import java.util.Map;
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
        List<GroupMember> all = groupMemberQueryRepository.findByGroup_IdIn(groupIds);

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

}
