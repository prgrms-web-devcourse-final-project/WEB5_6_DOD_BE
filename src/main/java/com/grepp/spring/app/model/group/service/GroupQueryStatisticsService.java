package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupStatisticsResponse;
import com.grepp.spring.app.model.group.repository.GroupMemberQueryRepository;
import com.grepp.spring.app.model.group.repository.GroupQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupQueryStatisticsService {

    private final GroupQueryRepository groupQueryRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;

    // 그룹 통계 조회
    public ShowGroupStatisticsResponse displayStatistics(Long groupId) {

        return null;
    }
    // TODO: 예외처리
    // id가 db에 없다
    // groupId가 db에 없다면 404_GROUP_NOT_FOUND
    // 현재 유저가 해당 그룹의 그룹원이 아니면 403_NOT_GROUP_MEMBER면 404_GROUP_NOT_FOUND


        //builder()
        //        .groupUserDetails(new ArrayList<>(List.of(
        //new GroupUserDetail("KAKAO_1001", "김우주", GroupRole.GROUP_LEADER, new ArrayList<>(List.of(30000L, 30002L, 30003L, 30004L, 30005L))),
        //new GroupUserDetail("KAKAO_1002", "백연우", GroupRole.GROUP_MEMBER, new ArrayList<>(List.of(30000L, 30002L))),
        //new GroupUserDetail("KAKAO_1003", "하예나", GroupRole.GROUP_MEMBER, new ArrayList<>(List.of(30000L, 30003L, 30005L))),
        //new GroupUserDetail("KAKAO_1004", "박민지", GroupRole.GROUP_MEMBER, new ArrayList<>(List.of(30000L, 30002L, 30004L, 30005L))),
        //new GroupUserDetail("KAKAO_1005", "성서아", GroupRole.GROUP_MEMBER, new ArrayList<>(List.of(30003L, 30004L, 30005L))),
        //new GroupUserDetail("KAKAO_1006", "허서영", GroupRole.GROUP_MEMBER, new ArrayList<>(List.of(30000L, 30002L, 30003L, 30004L, 30005L))),
        //new GroupUserDetail("KAKAO_1007", "최승현", GroupRole.GROUP_MEMBER, new ArrayList<>(List.of(30000L, 30002L, 30003L))),
        //new GroupUserDetail("KAKAO_1008", "박이안", GroupRole.GROUP_MEMBER, new ArrayList<>(List.of(30000L))),
        //new GroupUserDetail("KAKAO_1009", "전태오", GroupRole.GROUP_MEMBER, new ArrayList<>(List.of(30000L, 30004L, 30005L))),
        //new GroupUserDetail("KAKAO_1010", "신지우", GroupRole.GROUP_MEMBER, new ArrayList<>(List.of(30003L, 30004L, 30005L)))
        //)))
        //.groupSchedules(new ArrayList<>(List.of(
        //new GroupSchedule(30001L, "인천역", LocalDateTime.now(), LocalDateTime.MAX),
        //new GroupSchedule(30002L, "국제금융센터-부산은행역", LocalDateTime.now(), LocalDateTime.MAX),
        //new GroupSchedule(30003L, "합정역", LocalDateTime.now(), LocalDateTime.MAX),
        //new GroupSchedule(30004L, "역삼역", LocalDateTime.now(), LocalDateTime.MAX),
        //new GroupSchedule(30005L, "서초역", LocalDateTime.now(), LocalDateTime.MAX)
        //)))
        //.build();
}