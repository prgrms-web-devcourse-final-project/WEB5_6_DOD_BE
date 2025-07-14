package com.grepp.spring.app.model.group.service;


import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupCommandExileGroupMemberService {

    private final GroupCommandRepository groupCommandRepository;
    private final GroupMemberCommandRepository groupMemberCommandRepository;

    // 멤버 추방하기
    public void deportMember(Long groupId, String userId){


    }
    // TODO : 예외처리
    // groupId가 db에 없다면 404_GROUP_NOT_FOUND
    // userId가 db에 없다면 404_USER_NOT_FOUND
    // userId가 해당 그룹에 없다면 404_USER_NOT_IN_GROUP
    // 현재 유저가 해당 그룹의 그룹원이 아니면 403_NOT_GROUP_MEMBER
    // 현재 유저가 해당 그룹의 그룹장이 아니면 403_NOT_GROUP_OWNER
}
