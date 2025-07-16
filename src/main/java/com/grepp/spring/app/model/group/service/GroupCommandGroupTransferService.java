package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.request.ScheduleToGroupRequest;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberQueryRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.group.repository.GroupQueryRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.infra.error.exceptions.group.GroupNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.ScheduleAlreadyInGroupException;
import com.grepp.spring.infra.error.exceptions.group.ScheduleNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.UserNotInGroupException;
import com.grepp.spring.infra.response.GroupErrorCode;
import java.util.List;
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
public class GroupCommandGroupTransferService {

    private final GroupCommandRepository groupCommandRepository;
    private final GroupQueryRepository groupQueryRepository;
    private final MemberRepository memberRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final ScheduleMemberQueryRepository scheduleMemberQueryRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final GroupMemberCommandRepository groupMemberCommandRepository;
    private final EventRepository eventRepository;

    // 일회성 일정을 그룹으로 편입
    @Transactional
    public void transferSchedule(ScheduleToGroupRequest request) {

        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다: 401)

        Optional<Schedule> scheduleOptional = scheduleQueryRepository.findById(request.getScheduleId());
        // 예외 발생: 해당 schedule이 db에 없음: 404 SCHEDULE_NOT_FOUND
        if (scheduleOptional.isEmpty()) {
            throw new ScheduleNotFoundException(GroupErrorCode.SCHEDULE_NOT_FOUND);
        }

        Optional<Group> groupOptional = groupQueryRepository.findById(request.getGroupId());
        // 예외 발생: 해당 group은 존재하지 않음 - 404 GROUP_NOT_FOUND
        if(groupOptional.isEmpty()){
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }
        Group group = groupOptional.get();

        Schedule schedule = scheduleOptional.get();
        Event event = schedule.getEvent();
        Group group1 = event.getGroup();
        // 예외 발생: 일회성 일정이 아님: 409 SCHEDULE_ALREADY_IN_GROUP
        if (group1.getIsGrouped()) {
            throw new ScheduleAlreadyInGroupException(GroupErrorCode.SCHEDULE_ALREADY_IN_GROUP);
        }

        // 조회
        List<GroupMember> groupMembers = groupMemberQueryRepository.findByGroup(group);
        List<ScheduleMember> scheduleMembers = scheduleMemberQueryRepository.findByScheduleId(request.getScheduleId());
        for (ScheduleMember scheduleMember : scheduleMembers) {
            Member member1 = scheduleMember.getMember();
            boolean temp = false;
            for(GroupMember groupMember: groupMembers){
                if(member1.equals(groupMember.getMember())) {
                    temp = true;
                    break;
                }
            }
            // 예외 발생: 해당 그룹으로 편입시킬 수 없음 404 - USER_NOT_IN_GROUP
            if(!temp){
                throw new UserNotInGroupException(GroupErrorCode.USER_NOT_IN_GROUP);
            }
        }

        // 편입
        for(GroupMember groupMember: groupMembers) {
            groupMemberCommandRepository.deleteByGroupAndMemberId(group1, groupMember.getMember().getId());
        }


        event.getGroup().getEvents().remove(event); // 역방향에서도 제거
        event.setGroup(null);                      // 주인 쪽에서도 제거
        // 2. 새 group과 연결
        event.setGroup(group);
        group.getEvents().add(event);
        groupCommandRepository.delete(group1);
    }
}
