package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.request.CreateGroupRequest;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.group.dto.GroupCreateDto;
import com.grepp.spring.app.model.group.dto.GroupMemberCreateDto;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.group.repository.GroupRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper mapper;

    // 그룹 조회
    public void displayGroup(){
        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        log.info("멤버 조회");
        List<GroupMember> groupMember = groupMemberRepository.findByMemberId(member.getId());
        System.out.println(groupMember);
        for (GroupMember groupMember1: groupMember){

            System.out.println(groupMember1.getGroup().getId());
        }
    }

    // 그룹 생성
    @Transactional
    public void registGroup(CreateGroupRequest request){

        // 그룹 생성
        GroupCreateDto groupCreateDto = GroupCreateDto.toDto(request);
        Group group = GroupCreateDto.toEntity(groupCreateDto);
        groupRepository.save(group);
        log.info("그룹생성");

        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        log.info("멤버 조회");

        // 그룹-멤버 생성 (중간 테이블)
        GroupMember groupMember = GroupMemberCreateDto.toEntity(group, member);
        groupMemberRepository.save(groupMember);
        log.info("그룹-멤버 생성");
    }

}
