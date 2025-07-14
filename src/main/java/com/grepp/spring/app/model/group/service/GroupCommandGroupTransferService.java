package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.request.ScheduleToGroupRequest;
import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupCommandGroupTransferService {
    private final GroupCommandRepository groupCommandRepository;
    private final GroupQueryRepository groupQueryRepository;


    // 일회성 일정을 그룹으로 편입
    public void transferSchedule(ScheduleToGroupRequest request) {

    }
    // TODO : 예외처리
    // id가 db에 없다면 404_GROUP_NOT_FOUND
    // request의 scheduleId가 이미 그룹에 있다면 409_SCHEDULE_ALREADY_IN_GROUP
    // request의 scheduleId가 db에 없다면 404_SCHEDULE_NOT_FOUND
    // 현재 유저가 해당 일정의 구성원이 아니면 403_NOT_SCHEDULE_MEMBER
    // 현재 유저가 해당 일정의 팀장이 아니면 403_NOT_SCHEDULE_OWNER


    //        if(
    //            request.getGroupId()!=10001L && request.getGroupId()!=10002L && request.getGroupId()!=10003L &&
    //            request.getGroupId()!=10004L && request.getGroupId()!=10005L &&
    //            request.getGroupId()!=10006L
    //            ){
    //    return ResponseEntity.status(404)
    //        .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 그룹을 찾을 수 없습니다."));
    //}
    //            if(
    //                request.getScheduleId()==30001L || request.getScheduleId()==30002L || request.getScheduleId()==30003L ||
    //                request.getScheduleId()==30004L || request.getScheduleId()==30005L ||
    //                request.getScheduleId()==31111L || request.getScheduleId()==32222L || request.getScheduleId()==33333L || request.getScheduleId()==34444L
    //                ){
    //    return ResponseEntity.status(409)
    //        .body(ApiResponse.error(ResponseCode.CONFLICT_REGISTER, "이미 그룹에 존재하는 일정 입니다."));
    //}
    //            else if(
    //                request.getScheduleId()!=35555L && request.getScheduleId()!=36666L && request.getScheduleId()!=37777L &&
    //                request.getScheduleId()!=38888L && request.getScheduleId()!=39999L
    //                ){
    //    return ResponseEntity.status(404)
    //        .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다."));
    //}

}
