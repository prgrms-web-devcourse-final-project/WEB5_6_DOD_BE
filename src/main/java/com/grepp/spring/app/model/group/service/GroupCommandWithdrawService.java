package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupCommandWithdrawService {

    private final GroupCommandRepository groupCommandRepository;
    private final GroupQueryRepository groupQueryRepository;

    // 그룹 탈퇴
    public void withdrawGroup(Long groupId){

    }
    // TODO : 예외처리
    // groupId가 db에 없다면 404_GROUP_NOT_FOUND
    // 현재 유저가 해당 일정의 구성원이 아니면 403_NOT_SCHEDULE_MEMBER

}
