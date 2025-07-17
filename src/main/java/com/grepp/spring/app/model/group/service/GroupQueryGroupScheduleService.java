package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupScheduleResponse;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.dto.ScheduleDetails;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberQueryRepository;
import com.grepp.spring.app.model.group.repository.GroupQueryRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.infra.error.exceptions.group.GroupNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.NotGroupUserException;
import com.grepp.spring.infra.response.GroupErrorCode;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class GroupQueryGroupScheduleService {

    private final GroupQueryRepository groupQueryRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final ScheduleMemberQueryRepository scheduleMemberQueryRepository;

    // 그룹 일정 조회
    public ShowGroupScheduleResponse displayGroupSchedule(Long groupId) {
        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다: 401)

        Optional<Group> groupOptional = groupQueryRepository.findById(groupId);
        // 예외 발생: 해당 group은 존재하지 않음 - 404 GROUP_NOT_FOUND
        if(groupOptional.isEmpty()){
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }
        Group group = groupOptional.get();

        Optional<GroupMember> groupMemberOptional = groupMemberQueryRepository.findByGroupIdAndMemberId(groupId,
            member.getId());
        // 예외 발생: http 메서드를 요청한 유저가 해당 그룹의 그룹원이 아님 - 403 NOT_GROUP_MEMBER
        if(groupMemberOptional.isEmpty()){
            throw new NotGroupUserException(GroupErrorCode.NOT_GROUP_MEMBER);
        }

        // 그룹 정보 및 http 요청한 멤버의 그룹권한 조회
        String groupName = group.getName();
        String groupDescription = group.getDescription();
        Long groupMemberNumber = (long) groupMemberQueryRepository.findByGroupId(groupId).size();

        GroupRole groupRole = groupMemberOptional.get().getRole();

        ArrayList<ScheduleDetails> scheduleDetails = new ArrayList<>();
        // 해당 그룹의 일정들 조회
        for(Event event: eventRepository.findByGroupId(groupId)){
            MeetingType meetingType = event.getMeetingType();
            for(Schedule schedule: scheduleQueryRepository.findByEvent(event)){
                ScheduleDetails scheduleDetails1 = ScheduleDetails.builder()
                    .scheduleId(schedule.getId())
                    .scheduleName(schedule.getScheduleName())
                    .meetingType(meetingType)
                    .startTime(schedule.getStartTime())
                    .endTime(schedule.getEndTime())
                    .weekDay(schedule.getStartTime().getDayOfWeek())
                    .memberNames(
                        scheduleMemberQueryRepository.findByScheduleId(schedule.getId()).stream()
                            .map(ScheduleMember::getName)
                            .collect(Collectors.toCollection(ArrayList::new))
                    )
                    .build();
                scheduleDetails.add(scheduleDetails1);
            }
        }




        return ShowGroupScheduleResponse.builder()
            .groupId(groupId)
            .groupName(groupName)
            .groupDescription(groupDescription)
            .groupMemberNumbers(groupMemberNumber)
            .groupRole(groupRole)
            .scheduleDetails(scheduleDetails)
            .build();
    }
    // TODO : 예외처리
    // groupId가 db에 없다면 404_GROUP_NOT_FOUND
    // 현재 유저가 해당 그룹의 그룹원이 아니면 403_NOT_GROUP_MEMBER
}
