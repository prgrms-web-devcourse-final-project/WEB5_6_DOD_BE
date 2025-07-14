package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.request.ControlGroupRoleRequest;
import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupCommandModifyGroupRoleService {

    private final GroupCommandRepository groupCommandRepository;
    private final GroupMemberCommandRepository groupMemberCommandRepository;

    // 그룹 멤버 권한 수정
    public void modifyGroup(Long groupId, ControlGroupRoleRequest request){


    }
    // TODO : 예외처리
    // groupId가 db에 없다면 404_GROUP_NOT_FOUND
    // request의 userIds중 user들이 db에 없다면 404_USER_NOT_FOUND
    // userId가 해당 그룹에 없다면 404_USER_NOT_IN_GROUP
    // 현재 유저가 해당 그룹의 그룹원이 아니면 403_NOT_GROUP_MEMBER
    // 현재 유저가 해당 그룹의 그룹장이 아니면 403_NOT_GROUP_OWNER



    //        if(
    //            !request.getUserId().equals("KAKAO_1001") && !request.getUserId().equals("KAKAO_1002") && !request.getUserId().equals("KAKAO_1003") && !request.getUserId().equals("KAKAO_1004") && !request.getUserId().equals("KAKAO_1005") &&
    //    !request.getUserId().equals("KAKAO_1006") && !request.getUserId().equals("KAKAO_1007") && !request.getUserId().equals("KAKAO_1008") && !request.getUserId().equals("KAKAO_1009") && !request.getUserId().equals("KAKAO_1010") &&
    //    !request.getUserId().equals("GOOGLE_1001") && !request.getUserId().equals("GOOGLE_1002") && !request.getUserId().equals("GOOGLE_1003") && !request.getUserId().equals("GOOGLE_1004") &&
    //    !request.getUserId().equals("GOOGLE_1005") && !request.getUserId().equals("GOOGLE_1006") && !request.getUserId().equals("GOOGLE_1007") && !request.getUserId().equals("GOOGLE_1008")
    //        ){
    //    return ResponseEntity.status(404)
    //        .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 유저를 찾을 수 없습니다."));
    //}
    //        if(
    //            !request.getUserId().equals("KAKAO_1001") && !request.getUserId().equals("KAKAO_1002") && !request.getUserId().equals("KAKAO_1003") && !request.getUserId().equals("KAKAO_1004") && !request.getUserId().equals("KAKAO_1005") &&
    //    !request.getUserId().equals("KAKAO_1006") && !request.getUserId().equals("KAKAO_1007") && !request.getUserId().equals("KAKAO_1008") && !request.getUserId().equals("KAKAO_1009") && !request.getUserId().equals("KAKAO_1010")
    //        ){
    //    return ResponseEntity.status(404)
    //        .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 유저를 그룹에서 찾을 수 없습니다."));
    //}

}
