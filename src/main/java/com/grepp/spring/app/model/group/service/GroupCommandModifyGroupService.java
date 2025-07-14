package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.request.ModifyGroupInfoRequest;
import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupCommandModifyGroupService {

    private final GroupCommandRepository groupCommandRepository;
    private final GroupMemberCommandRepository groupMemberCommandRepository;

    // 그룹 정보 수정
    public void modifyGroup(Long groupId, ModifyGroupInfoRequest request){


    }
    // TODO : 예외처리
    // id가 db에 없다면 404_GROUP_NOT_FOUND
    // 현재 유저가 해당 그룹의 그룹원이 아니면 403_NOT_GROUP_MEMBER
    // 현재 유저가 해당 그룹의 그룹장이 아니면 403_NOT_GROUP_OWNER
}
