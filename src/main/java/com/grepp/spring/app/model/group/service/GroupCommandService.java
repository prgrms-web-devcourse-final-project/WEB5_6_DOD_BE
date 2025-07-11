package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.request.CreateGroupRequest;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.group.dto.GroupCreateDto;
import com.grepp.spring.app.model.group.dto.GroupMemberCreateDto;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberCommandRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupCommandService {
    private final GroupCommandRepository groupCommandRepository;
    private final GroupMemberCommandRepository groupMemberCommandRepository;
    // TODO: memberRepository에도 CQRS를 적용한다면, memberRepository 대신 memberQueryRepository로 변환
    // member를 가져오는 전략
    // 1. Controller에서 MemberId로 Member객체를 조회한 후, 매개변수에 담아 Service로 Member객체를 넘겨줌.
        // Controller와 Entity가 연결되어 계층끼리의 결합도 상승(Group
    // 2. Service에서 Member객체의 Id 필드에만 접근한다면 Member 테이블에 select쿼리문이 날아가지 않아도 Member객체에 접근 가능
    // Member엔티티에 static의 ofId메서드를 작성하여 활용
        // -> member조회가 CommandService에 혼재될 일이 없음.
    // 3. 현재의 Service에서 MemberQueryRepository를 주입받아서 활용
        // memberQueryRepository.getReferenceById 메서드 활용(프록시로 조회하기에 select쿼리문이 날아가지 않음)
        // CQRS 아키텍처가 깨짐(Group"Command"Service에서 Member"Query"Repository 활용)
    // 좀 더 고민해보자.
    private final MemberRepository memberRepository;

    // 그룹 생성
    @Transactional
    public void registGroup(CreateGroupRequest request){

        // 그룹 생성
        GroupCreateDto groupCreateDto = GroupCreateDto.toDto(request);
        Group group = GroupCreateDto.toEntity(groupCreateDto);
        groupCommandRepository.save(group);
        log.info("그룹생성 CommandService");

        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        log.info("멤버 조회 CommandService");

        // 그룹-멤버 생성 (중간 테이블)
        GroupMember groupMember = GroupMemberCreateDto.toEntity(group, member);
        groupMemberCommandRepository.save(groupMember);
        log.info("그룹-멤버 생성 CommandService");
    }
}
