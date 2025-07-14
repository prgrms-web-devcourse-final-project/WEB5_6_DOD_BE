package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupCommandDeleteGroupService {

    private final GroupCommandRepository groupCommandRepository;
    private final GroupMemberCommandRepository groupMemberCommandRepository;

    // 그룹 삭제
    public void deleteGroup(Long groupId){


    }
    // TODO : 예외처리
    // 현재 유저가 해당 그룹의 그룹원이 아니면 403_NOT_GROUP_MEMBER
    // 현재 유저가 해당 그룹의 그룹장이 아니면 403_NOT_GROUP_OWNER
}
