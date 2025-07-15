package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberQueryRepository;
import com.grepp.spring.app.model.group.repository.GroupQueryRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.infra.error.exceptions.group.GroupNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.NotGroupUserException;
import com.grepp.spring.infra.error.exceptions.group.OnlyOneGroupLeaderException;
import com.grepp.spring.infra.response.GroupErrorCode;
import java.util.ArrayList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupCommandWithdrawService {

    private final GroupCommandRepository groupCommandRepository;
    private final GroupQueryRepository groupQueryRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final EventRepository eventRepository;
    private final ScheduleCommandRepository scheduleCommandRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final ScheduleMemberQueryRepository scheduleMemberQueryRepository;
    private final GroupMemberCommandRepository groupMemberCommandRepository;
    private final EventMemberRepository eventMemberRepository;
    private final ScheduleMemberCommandRepository scheduleMemberCommandRepository;

    // 그룹 탈퇴
    @Transactional
    public void withdrawGroup(Long groupId){
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
        GroupMember groupMember = groupMemberOptional.get();

        // 해당 그룹의 리더들 조회
        ArrayList<GroupMember> groupLeaders = groupMemberQueryRepository.findByGroupAndRole(group, GroupRole.GROUP_LEADER);
        ArrayList<GroupMember> groupMembers = groupMemberQueryRepository.findByGroupAndRole(group, GroupRole.GROUP_MEMBER);
        // 예외 발생: 해당 그룹의 리더가 1명이고 그룹원이 있는 경우 - 409 ONLY_ONE_LEADER
        if(groupMember.getRole().equals(GroupRole.GROUP_LEADER)
            && groupLeaders.size()==1
            && !groupMembers.isEmpty()){
            throw new OnlyOneGroupLeaderException(GroupErrorCode.ONE_GROUP_LEADER);
        }

        // 해당 그룹의 슈퍼 리더 이면서 그룹 내에 리더가 여러 명일 경우 -> 슈퍼 리더를 랜덤하게 타 groupLeader에게 이양
        if(groupMember.getRole().equals(GroupRole.GROUP_LEADER)
            && groupMember.getGroupAdmin()
            && groupLeaders.size()>1){
            // 그룹 슈러 리더 한 번 전달 후 반복문 탈출
            for(GroupMember groupLeader1: groupLeaders){
                if(groupLeader1.equals(groupMember)) continue;
                groupLeader1.setGroupAdmin(true);
                break;
            }
        }

        // 삭제 진행
        // groupMember 삭제
        groupMemberCommandRepository.delete(group, member.getId());
        // 그룹 내 이벤트에 대한 처리
        for(Event event: eventRepository.findByGroupId(groupId)){
            // eventMember 삭제
            eventMemberRepository.delete(event, member.getId());
            // 이벤트 내 일정에 대한 처리
            for(Schedule schedule: scheduleQueryRepository.findByEnvet(event)){
                // scheduleMember 삭제
                // 일정에 본인이 일정 팀장인 경우 isRoleMaster를 true로 설정
                boolean isRoleMaster = false;
                if(scheduleMemberQueryRepository
                    .findByEventAndMemberId(event, member.getId())
                    .getRole()
                    .equals(ScheduleRole.ROLE_MASTER)){
                    isRoleMaster = true;
                }
                scheduleMemberCommandRepository.delete(schedule, member.getId());
                // 일정에 본인만 포함된 경우 -> schedule 삭제
                if(scheduleMemberQueryRepository.findBySchedule(schedule).isEmpty()){
                    scheduleCommandRepository.delete(schedule);
                }
                // 일정에 본인이 일정 팀장인 경우 -> 팀장 권한 랜덤으로 넘기기
                else if (isRoleMaster) {
                    ArrayList<ScheduleMember> scheduleMembers = scheduleMemberQueryRepository.findBySchedule(schedule);
                    // 일정 팀장 권한 한 번 전달 후 반복문 탈출
                    for(ScheduleMember scheduleMember1: scheduleMembers){
                        scheduleMember1.setRole(ScheduleRole.ROLE_MASTER);
                        break;
                    }
                }
            }
            // 이벤트에 본인만 포함된 경우 -> Event 삭제
            if(eventMemberRepository.findByEvent(event).isEmpty()){
                eventRepository.delete(event);
            }
        }
        // 그룹에 본인만 포함된 경우 -> group 삭제
        if(groupMemberQueryRepository.findByGroup(group).isEmpty()){
            groupCommandRepository.delete(group);
        }
    }
}
