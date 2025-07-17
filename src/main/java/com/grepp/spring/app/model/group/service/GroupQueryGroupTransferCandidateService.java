package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.response.ShowCandidateGroupResponse;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.group.dto.GroupCandidateDto;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.group.repository.GroupQueryRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.infra.error.exceptions.group.ScheduleAlreadyInGroupException;
import com.grepp.spring.infra.error.exceptions.group.ScheduleNotFoundException;
import com.grepp.spring.infra.response.GroupErrorCode;
import java.util.ArrayList;
import java.util.HashSet;
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
public class GroupQueryGroupTransferCandidateService {
    private final MemberRepository memberRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final EventRepository eventRepository;
    private final ScheduleMemberQueryRepository scheduleMemberQueryRepository;
    private final GroupQueryRepository groupQueryRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public ShowCandidateGroupResponse transferCandidateSchedule(Long id) {

        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다: 401)

        Optional<Schedule> scheduleOptional = scheduleQueryRepository.findById(id);
        // 예외 발생: 해당 schedule이 db에 없음: 404 SCHEDULE_NOT_FOUND
        if (scheduleOptional.isEmpty()){
            throw new ScheduleNotFoundException(GroupErrorCode.SCHEDULE_NOT_FOUND);
        }

        Schedule schedule = scheduleOptional.get();
        Event event = schedule.getEvent();
        Group group = event.getGroup();
        // 예외 발생: 일회성 일정이 아님: 409 SCHEDULE_ALREADY_IN_GROUP
        if(group.getIsGrouped()){
            throw new ScheduleAlreadyInGroupException(GroupErrorCode.SCHEDULE_ALREADY_IN_GROUP);
        }

        // 조회
        List<ScheduleMember> scheduleMembers = scheduleMemberQueryRepository.findByScheduleId(id);
        boolean temp = true;
        HashSet<Long> hashSet = new HashSet<>();
        for(ScheduleMember scheduleMember: scheduleMembers){
            List<GroupMember> groupMembers =  groupMemberRepository.findByMemberId(scheduleMember.getMember().getId());
            HashSet<Long> memberGroupSet = new HashSet<>();
            for(GroupMember groupMember: groupMembers){
                memberGroupSet.add(groupMember.getGroup().getId());
            }
            if(temp){
                hashSet.addAll(memberGroupSet);
            }
            else{
                hashSet.retainAll(memberGroupSet);
                continue;
            }
            temp = false;
        }

        ArrayList<GroupCandidateDto> groupCandidateDtos = new ArrayList<>();
        for(Long groupId: hashSet){
            Group group1 = groupQueryRepository.findById(groupId).get();
            if(group1.getIsGrouped()) {
                groupCandidateDtos.add(
                    new GroupCandidateDto(groupId, group1.getName(), group1.getDescription()));
            }
        }

        return ShowCandidateGroupResponse.builder()
            .candidateGroups(groupCandidateDtos)
            .build();
    }
}
