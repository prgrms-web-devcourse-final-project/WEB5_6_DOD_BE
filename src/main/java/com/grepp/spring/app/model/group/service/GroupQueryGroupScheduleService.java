package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupScheduleResponse;
import com.grepp.spring.app.model.group.repository.GroupMemberQueryRepository;
import com.grepp.spring.app.model.group.repository.GroupQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class GroupQueryGroupScheduleService {

    private final GroupQueryRepository groupQueryRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;

    // 그룹 일정 조회
    public ShowGroupScheduleResponse displayGroupSchedule(Long groupId) {

        return null;
    }
    // TODO : 예외처리
    // groupId가 db에 없다면 404_GROUP_NOT_FOUND
    // 현재 유저가 해당 그룹의 그룹원이 아니면 403_NOT_GROUP_MEMBER
}
