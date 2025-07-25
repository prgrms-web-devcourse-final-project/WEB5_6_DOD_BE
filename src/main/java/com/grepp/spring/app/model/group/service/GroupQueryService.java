package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.response.ShowCandidateGroupResponse;
import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupMemberResponse;
import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupScheduleResponse;
import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupStatisticsResponse;
import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventQueryRepository;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.dto.GroupCandidateDto;
import com.grepp.spring.app.model.group.dto.GroupSchedule;
import com.grepp.spring.app.model.group.dto.GroupUser;
import com.grepp.spring.app.model.group.dto.GroupUserDetail;
import com.grepp.spring.app.model.group.dto.ScheduleDetails;
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
import com.grepp.spring.infra.error.exceptions.group.GroupAuthenticationException;
import com.grepp.spring.infra.error.exceptions.group.GroupNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.NotGroupLeaderException;
import com.grepp.spring.infra.error.exceptions.group.NotGroupUserException;
import com.grepp.spring.infra.error.exceptions.group.ScheduleAlreadyInGroupException;
import com.grepp.spring.infra.error.exceptions.group.ScheduleNotFoundException;
import com.grepp.spring.infra.response.GroupErrorCode;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupQueryService {

    private final MemberRepository memberRepository;
    private final GroupQueryRepository groupQueryRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final EventQueryRepository eventQueryRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final ScheduleMemberQueryRepository scheduleMemberQueryRepository;


    // 그룹 일정 조회
    public ShowGroupScheduleResponse displayGroupSchedule(Long groupId) {
        // http 요청 사용자 조회 - 401 AUTHENTICATED_REQUIRED 예외 처리
        Member member = extractCurrentMember();
        // 그룹 조회 - 404 GROUP_NOT_FOUND 예외 처리
        Group group = extractGroup(groupId);
        // 그룹멤버 조회 - 403 NOT_GROUP_MEMBER 예외 처리
        GroupMember groupMember = extractGroupMember(groupId, member.getId());

        // 로직 시작
        //## 그룹 일정 조회
        // 그룹 정보 및 http 요청한 멤버의 그룹권한 조회
        String groupName = group.getName();
        String groupDescription = group.getDescription();
        Long groupMemberNumber = (long) groupMemberQueryRepository.findByGroupId(groupId).size();

        GroupRole groupRole = groupMember.getRole();

        ArrayList<ScheduleDetails> scheduleDetails = new ArrayList<>();
        // 해당 그룹의 일정들 조회
        for (Event event : eventQueryRepository.findByGroupId(groupId)) {
            MeetingType meetingType = event.getMeetingType();
            for (Schedule schedule : scheduleQueryRepository.findByEvent(event)) {
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


    // 그룹 멤버 조회
    public ShowGroupMemberResponse displayGroupMember(Long groupId) {
        // http 요청 사용자 조회 - 401 AUTHENTICATED_REQUIRED 예외 처리
        Member member = extractCurrentMember();
        // 그룹 조회 - 404 GROUP_NOT_FOUND 예외 처리
        Group group = extractGroup(groupId);
        // 그룹멤버 조회 - 403 NOT_GROUP_MEMBER 예외 처리
        GroupMember groupMember = extractGroupMember(groupId, member.getId());

        // 로직 시작
        //## 그룹 멤버 조회
        ShowGroupMemberResponse response = new ShowGroupMemberResponse();
        // http요청으로 요청된 group의 group-member들 조회
        List<GroupMember> groupMembers = groupMemberQueryRepository.findByGroup(group);
        // response의 필드인 groupUser 리스트 초기화
        ArrayList<GroupUser> groupUsers = response.getGroupUser();
        // groupMember들을 순회하면서 그룹별 멤버Id/이름/권한을 저장
        for (GroupMember groupMember1 : groupMembers) {
            String memberId = groupMember1.getMember().getId();
            String memberName = memberRepository.findById(memberId).get().getName();
            Integer profileImageNumber = memberRepository.findById(memberId).get()
                .getProfileImageNumber();
            GroupRole groupRole = groupMember1.getRole();
            // 멤버Id, 이름, 권한으로 GroupUser 리스트에 추가
            groupUsers.add(new GroupUser(memberId, memberName, profileImageNumber, groupRole));
        }

        return response;
    }


    // 그룹 통계 조회
    @Transactional(readOnly = true)
    public ShowGroupStatisticsResponse displayStatistics(Long groupId) {
        // http 요청 사용자 조회 - 401 AUTHENTICATED_REQUIRED 예외 처리
        Member member = extractCurrentMember();
        // 그룹 조회 - 404 GROUP_NOT_FOUND 예외 처리
        Group group = extractGroup(groupId);
        // 그룹멤버 조회 - 403 NOT_GROUP_MEMBER 예외 처리
        GroupMember groupMember = extractGroupMember(groupId, member.getId());
        // 그룹멤버 리더 권한 조회 - 403 NOT_GROUP_LEADER 예외 처리
        checkRoleGroupLeader(groupMember);

        // 로직 시작
        //## 그룹 통계 조회
        long scheduleNum = 0L;
        HashMap<String, Long> locationMap = new HashMap<>();
        HashMap<String, Long> memberMap = new HashMap<>();
        long[] weekDayArray = new long[7];
        for (Event event : eventQueryRepository.findByGroupId(groupId)) {
            for (Schedule schedule : scheduleQueryRepository.findByEvent(event)) {
                scheduleNum += 1L;
                String location = schedule.getLocation();
                if (!locationMap.containsKey(location)) {
                    locationMap.put(location, 1L);
                } else {
                    locationMap.put(location, locationMap.get(location) + 1L);
                }
                if (schedule.getStartTime().getDayOfWeek().equals(DayOfWeek.MONDAY)) {
                    weekDayArray[0] += 1L;
                } else if (schedule.getStartTime().getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
                    weekDayArray[1] += 1L;
                } else if (schedule.getStartTime().getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
                    weekDayArray[2] += 1L;
                } else if (schedule.getStartTime().getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
                    weekDayArray[3] += 1L;
                } else if (schedule.getStartTime().getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
                    weekDayArray[4] += 1L;
                } else if (schedule.getStartTime().getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                    weekDayArray[5] += 1L;
                } else {
                    weekDayArray[6] += 1L;
                }
                for (ScheduleMember scheduleMember : scheduleMemberQueryRepository.findBySchedule(
                    schedule)) {
                    if (!memberMap.containsKey(scheduleMember.getMember().getName())) {
                        memberMap.put(scheduleMember.getMember().getName(), 1L);
                    } else {
                        memberMap.put(scheduleMember.getMember().getName(),
                            memberMap.get(scheduleMember.getMember().getName()) + 1L);
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


    //## 일회성 일정이 들어갈 수 있는 후보 그룹 조회
    @Transactional(readOnly = true)
    public ShowCandidateGroupResponse transferCandidateSchedule(Long id) {
        // http 요청 사용자 조회 - 401 AUTHENTICATED_REQUIRED 예외 처리
        Member member = extractCurrentMember();
        // 일정 조회 - 404 SCHEDULE_NOT_FOUND 예외 처리
        Schedule schedule = extractSchedule(id);

        Event event = schedule.getEvent();
        Group group = event.getGroup();
        // 일정 일회성 조회 - 409 SCHEDULE_ALREADY_IN_GROUP
        if (group.getIsGrouped()) {
            throw new ScheduleAlreadyInGroupException(GroupErrorCode.SCHEDULE_ALREADY_IN_GROUP);
        }

        // 로직 시작
        //## 후보 그룹 조회
        List<ScheduleMember> scheduleMembers = scheduleMemberQueryRepository.findByScheduleId(id);
        boolean temp = true;
        HashSet<Long> hashSet = new HashSet<>();
        for (ScheduleMember scheduleMember : scheduleMembers) {
            List<GroupMember> groupMembers = groupMemberQueryRepository.findByMemberId(
                scheduleMember.getMember().getId());
            HashSet<Long> memberGroupSet = new HashSet<>();
            for (GroupMember groupMember : groupMembers) {
                memberGroupSet.add(groupMember.getGroup().getId());
            }
            if (temp) {
                hashSet.addAll(memberGroupSet);
            } else {
                hashSet.retainAll(memberGroupSet);
                continue;
            }
            temp = false;
        }

        ArrayList<GroupCandidateDto> groupCandidateDtos = new ArrayList<>();
        for (Long groupId : hashSet) {
            Group group1 = extractGroup(groupId);
            if (group1.getIsGrouped()) {
                groupCandidateDtos.add(
                    new GroupCandidateDto(groupId, group1.getName(), group1.getDescription()));
            }
        }

        return ShowCandidateGroupResponse.builder()
            .candidateGroups(groupCandidateDtos)
            .build();
    }


    /// 범용적인 예외처리 메서드
    // http 메서드 요청한 member 조회 - 401 AUTHENTICATION_REQUIRED 예외 처리
    private Member extractCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
            !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {

            throw new GroupAuthenticationException(GroupErrorCode.AUTHENTICATION_REQUIRED);
        }
        Optional<Member> memberOptional = memberRepository.findById(authentication.getName());
        if (memberOptional.isPresent()) {
            return memberOptional.get();
        }
        throw new GroupAuthenticationException(GroupErrorCode.AUTHENTICATION_REQUIRED);
    }


    // 그룹 조회 - 404 GROUP_NOT_FOUND 예외 처리
    private Group extractGroup(Long groupId) {
        Optional<Group> groupOptional = groupQueryRepository.findById(groupId);
        if (groupOptional.isEmpty()) {
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }
        return groupOptional.get();
    }


    // 일정 조회 - 404 SCHEDULE_NOT_FOUND 예외 처리
    private Schedule extractSchedule(Long scheduleId) {
        Optional<Schedule> scheduleOptional = scheduleQueryRepository.findById(
            scheduleId);
        if (scheduleOptional.isEmpty()) {
            throw new ScheduleNotFoundException(GroupErrorCode.SCHEDULE_NOT_FOUND);
        }
        return scheduleOptional.get();
    }


    // 일정 조회 - 404 SCHEDULE_NOT_FOUND 예외 처리
    private GroupMember extractGroupMember(Long groupId, String id) {
        Optional<GroupMember> groupMemberOptional = groupMemberQueryRepository.findByGroupIdAndMemberId(
            groupId, id);
        if (groupMemberOptional.isEmpty()) {
            throw new NotGroupUserException(GroupErrorCode.NOT_GROUP_MEMBER);
        }
        return groupMemberOptional.get();
    }


    // 그룹멤버 리더 권한 조회 - 403 NOT_GROUP_LEADER 예외 처리
    private void checkRoleGroupLeader(GroupMember groupMember) {
        if (!groupMember.getRole().equals(GroupRole.GROUP_LEADER)) {
            throw new NotGroupLeaderException(GroupErrorCode.NOT_GROUP_LEADER);
        }
    }


}
