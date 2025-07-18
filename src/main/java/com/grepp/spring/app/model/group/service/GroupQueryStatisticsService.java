package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupStatisticsResponse;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.dto.GroupSchedule;
import com.grepp.spring.app.model.group.dto.GroupUserDetail;
import com.grepp.spring.app.model.group.dto.WeekDetail;
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
import com.grepp.spring.infra.error.exceptions.group.NotGroupLeaderException;
import com.grepp.spring.infra.error.exceptions.group.NotGroupUserException;
import com.grepp.spring.infra.response.GroupErrorCode;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
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
public class GroupQueryStatisticsService {

    private final GroupQueryRepository groupQueryRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final ScheduleMemberQueryRepository scheduleMemberQueryRepository;

    // 그룹 통계 조회
    public ShowGroupStatisticsResponse displayStatistics(Long groupId) {
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

        Optional<GroupMember> groupMemberOptional = groupMemberQueryRepository.findByGroupIdAndMemberId(groupId,
            member.getId());
        // 예외 발생: http 메서드를 요청한 유저가 해당 그룹의 그룹원이 아님 - 403 NOT_GROUP_MEMBER
        if(groupMemberOptional.isEmpty()){
            throw new NotGroupUserException(GroupErrorCode.NOT_GROUP_MEMBER);
        }
        GroupMember groupMember = groupMemberOptional.get();

        // 예외 발생: http 메서드를 요청한 유저가 해당 그룹의 그룹장이 아님 - 403 NOT_GROUP_LEADER
        if(!groupMember.getRole().equals(GroupRole.GROUP_LEADER)){
            throw new NotGroupLeaderException(GroupErrorCode.NOT_GROUP_LEADER);
        }

        // 그룹 통계 조회
        long scheduleNum = 0L;
        HashMap<String, Long> locationMap = new HashMap<>();
        HashMap<String, Long> memberMap = new HashMap<>();
        long[] weekDayArray = new long[7];
        for(Event event: eventRepository.findByGroupId(groupId)){
            for(Schedule schedule: scheduleQueryRepository.findByEvent(event)){
                scheduleNum +=1L;
                String location = schedule.getLocation();
                if (!locationMap.containsKey(location)){
                    locationMap.put(location, 1L);
                }
                else{
                    locationMap.put(location,locationMap.get(location)+1L);
                }
                if(schedule.getStartTime().getDayOfWeek().equals(DayOfWeek.MONDAY)){
                    weekDayArray[0]+=1L;
                } else if (schedule.getStartTime().getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
                    weekDayArray[1]+=1L;
                }else if (schedule.getStartTime().getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
                    weekDayArray[2]+=1L;
                }else if (schedule.getStartTime().getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
                    weekDayArray[3]+=1L;
                }else if (schedule.getStartTime().getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
                    weekDayArray[4]+=1L;
                }else if (schedule.getStartTime().getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                    weekDayArray[5]+=1L;
                }else{
                    weekDayArray[6]+=1L;
                }
                for(ScheduleMember scheduleMember: scheduleMemberQueryRepository.findBySchedule(schedule)){
                    if(!memberMap.containsKey(scheduleMember.getMember().getName())){
                        memberMap.put(scheduleMember.getMember().getName(), 1L);
                    }else{
                        memberMap.put(scheduleMember.getMember().getName(), memberMap.get(scheduleMember.getMember().getName())+1L);
                    }
                }
            }
        }

        ArrayList<WeekDetail> weekDetails = new ArrayList<>();

        ArrayList<GroupUserDetail> userDetails = memberMap.entrySet().stream()
            .map(entry -> new GroupUserDetail(entry.getKey(), entry.getValue()))
            .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<GroupSchedule> groupSchedules = locationMap.entrySet().stream()
            .map(entry -> new GroupSchedule(entry.getKey(), entry.getValue()))
            .collect(Collectors.toCollection(ArrayList::new));

        weekDetails.add(new WeekDetail(DayOfWeek.MONDAY, weekDayArray[0]));
        weekDetails.add(new WeekDetail(DayOfWeek.TUESDAY, weekDayArray[1]));
        weekDetails.add(new WeekDetail(DayOfWeek.WEDNESDAY, weekDayArray[2]));
        weekDetails.add(new WeekDetail(DayOfWeek.THURSDAY, weekDayArray[3]));
        weekDetails.add(new WeekDetail(DayOfWeek.FRIDAY, weekDayArray[4]));
        weekDetails.add(new WeekDetail(DayOfWeek.SATURDAY, weekDayArray[5]));
        weekDetails.add(new WeekDetail(DayOfWeek.SUNDAY, weekDayArray[6]));

        return ShowGroupStatisticsResponse.builder()
            .scheduleNumber(scheduleNum)
            .groupUserDetails(userDetails)
            .groupSchedules(groupSchedules)
            .weekDetails(weekDetails)
            .build();
    }
}