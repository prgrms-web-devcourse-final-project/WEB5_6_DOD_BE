package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.request.ControlGroupRoleRequest;
import com.grepp.spring.app.controller.api.group.payload.request.CreateGroupRequest;
import com.grepp.spring.app.controller.api.group.payload.request.ModifyGroupInfoRequest;
import com.grepp.spring.app.controller.api.group.payload.request.ScheduleToGroupRequest;
import com.grepp.spring.app.controller.api.group.payload.response.CreateGroupResponse;
import com.grepp.spring.app.controller.api.group.payload.response.InviteGroupMemberResponse;
import com.grepp.spring.app.controller.api.group.payload.response.ModifyGroupInfoResponse;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.dto.GroupCreateDto;
import com.grepp.spring.app.model.group.dto.GroupMemberCreateDto;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberCommandRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberCommandRepository;
import com.grepp.spring.infra.error.exceptions.group.GroupNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.NotGroupLeaderException;
import com.grepp.spring.infra.error.exceptions.group.NotGroupUserException;
import com.grepp.spring.infra.error.exceptions.group.OnlyOneGroupLeaderException;
import com.grepp.spring.infra.error.exceptions.group.ScheduleAlreadyInGroupException;
import com.grepp.spring.infra.error.exceptions.group.ScheduleNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.UserAlreadyInGroupException;
import com.grepp.spring.infra.error.exceptions.group.UserGroupLeaderException;
import com.grepp.spring.infra.error.exceptions.group.UserNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.UserNotInGroupException;
import com.grepp.spring.infra.response.GroupErrorCode;
import java.util.ArrayList;
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
public class GroupCommandService {

    private final MemberRepository memberRepository;
    private final GroupCommandRepository groupCommandRepository;
    private final GroupMemberCommandRepository groupMemberCommandRepository;
    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final ScheduleCommandRepository scheduleCommandRepository;
    private final ScheduleMemberCommandRepository scheduleMemberCommandRepository;

    // TODO: memberRepository에도 CQRS를 적용한다면, memberRepository 대신 memberQueryRepository로 변환
    // member를 가져오는 전략
    // 1. Controller에서 MemberId로 Member객체를 조회한 후, 매개변수에 담아 Service로 Member객체를 넘겨줌.
    // Controller와 Entity가 연결되어 계층끼리의 결합도 상승(Group
    // 2. Service에서 Member객체의 Id 필드에만 접근한다면 Member 테이블에 select쿼리문이 날아가지 않아도 Member객체에 접근 가능
    // Member엔티티에 static의 ofId메서드를 작성하여 활용
    // -> member조회가 CommandService에 혼재될 일이 없음.
    // 3. 현재의 Service에서 MemberQueryRepository를 주입받아서 활용
    // memberQueryRepository.getReferenceById 메서드 활용(프록시로 조회하기에 select쿼리문이 날아가지 않음)
    // CQRS 아키텍처가 깨짐(Group"Command"Service에서 Member"Query"Repository 활용)
    // 좀 더 고민해보자.


    // 그룹 생성
    @Transactional
    public CreateGroupResponse registGroup(CreateGroupRequest request) {
        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다.)

        //## 그룹 생성
        GroupCreateDto groupCreateDto = GroupCreateDto.toDto(request);
        Group group = GroupCreateDto.toEntity(groupCreateDto);
        groupCommandRepository.save(group);
        // 그룹-멤버 생성 (중간 테이블)
        GroupMember groupMember = GroupMemberCreateDto.toEntity(group, member);
        groupMemberCommandRepository.save(groupMember);
        // 그룹 생성 후 정보 반환
        return CreateGroupResponse.builder()
            .groupId(group.getId())
            .groupName(group.getName())
            .description(group.getDescription())
            .build();
    }


    // 그룹 멤버 추가
    @Transactional
    public InviteGroupMemberResponse addGroupMember(Long groupId) {
        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다.)

        Optional<Group> groupOptional = groupCommandRepository.findById(groupId);
        // 예외 발생: 해당 group은 존재하지 않음 - 404 GROUP_NOT_FOUND
        if (groupOptional.isEmpty()) {
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }
        Group group = groupOptional.get();

        ArrayList<GroupMember> groupMembers = groupMemberCommandRepository.findByGroupId(groupId);
        for (GroupMember groupMember : groupMembers) {
            // 예외 발생: 이미 그룹에 있음 - 409 USER_ALREADY_IN_GROUP
            if (groupMember.getMember().getId().equals(member.getId())) {
                throw new UserAlreadyInGroupException(GroupErrorCode.USER_ALREADY_IN_GROUP);
            }
        }

        //## 그룹에 멤버 추가
        // 그룹-멤버 생성 (중간 테이블)
        GroupMember groupMember = GroupMemberCreateDto.toEntity(group, member);
        groupMember.setGroupAdmin(false);
        groupMember.setRole(GroupRole.GROUP_MEMBER);
        groupMemberCommandRepository.save(groupMember);
        log.info("그룹-멤버 생성 CommandService");
        // 그룹 수정 후 정보 반환
        return InviteGroupMemberResponse.builder()
            .memberId(member.getId())
            .memberName(member.getName())
            .groupId(groupId)
            .build();
    }


    // 그룹 삭제
    @Transactional
    public void deleteGroup(Long groupId) {
        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다: 401)

        Optional<Group> groupOptional = groupCommandRepository.findById(groupId);
        // 예외 발생: 해당 group은 존재하지 않음 - 404 GROUP_NOT_FOUND
        if (groupOptional.isEmpty()) {
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }

        Optional<GroupMember> groupMemberOptional = groupMemberCommandRepository.findByGroupIdAndMemberId(
            groupId,
            member.getId());
        // 예외 발생: http 메서드를 요청한 유저가 해당 그룹의 그룹원이 아님 - 403 NOT_GROUP_MEMBER
        if (groupMemberOptional.isEmpty()) {
            throw new NotGroupUserException(GroupErrorCode.NOT_GROUP_MEMBER);
        }
        GroupMember groupMember = groupMemberOptional.get();

        // 예외 발생: http 메서드를 요청한 유저가 해당 그룹의 그룹장이 아님 - 403 NOT_GROUP_LEADER
        if (!groupMember.getRole().equals(GroupRole.GROUP_LEADER)) {
            throw new NotGroupLeaderException(GroupErrorCode.NOT_GROUP_LEADER);
        }

        //## 그룹 삭제 진행, cascade 삭제 진행
        groupCommandRepository.deleteById(groupId);
    }


    // 그룹 정보 수정
    @Transactional
    public ModifyGroupInfoResponse modifyGroup(Long groupId, ModifyGroupInfoRequest request) {
        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다.)

        Optional<Group> group = groupCommandRepository.findById(groupId);
        // 예외 발생: 해당 group은 존재하지 않음 - 404 GROUP_NOT_FOUND
        if (group.isEmpty()) {
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }
        Group group1 = group.get();

        Optional<GroupMember> groupMember = groupMemberCommandRepository.findByGroupIdAndMemberId(
            groupId, member.getId());
        // 예외 발생: 해당 group의 그룹원이 아닌 경우 - USER_NOT_IN_GROUP
        if (groupMember.isEmpty()) {
            throw new UserNotInGroupException(GroupErrorCode.USER_NOT_IN_GROUP);
        }

        // 예외 발생: 해당 group의 그룹장이 아닌 경우 - NOT_GROUP_LEADER
        if (!groupMember.get().getRole().equals(GroupRole.GROUP_LEADER)) {
            throw new NotGroupLeaderException(GroupErrorCode.NOT_GROUP_LEADER);
        }

        //## patch 메서드 진행
        if (!request.getGroupName().isEmpty()) {
            group1.setName(request.getGroupName());
        }
        if (!request.getDescription().isEmpty()) {
            group1.setDescription(request.getDescription());
        }
        // 수정 사항 저장
        groupCommandRepository.save(group1);

        return ModifyGroupInfoResponse.builder()
            .groupId(group1.getId())
            .groupName(group1.getName())
            .description(group1.getDescription())
            .build();
    }


    // 멤버 추방하기
    @Transactional
    public void deportMember(Long groupId, String userId) {
        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다: 401)

        Optional<Group> groupOptional = groupCommandRepository.findById(groupId);
        // 예외 발생: 해당 group은 존재하지 않음 - 404 GROUP_NOT_FOUND
        if (groupOptional.isEmpty()) {
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }
        Group group = groupOptional.get();

        // TODO: 본인을 추방하는 예외 추가하기 -> 탈퇴랑 똑같음
        Optional<GroupMember> groupMemberOptional = groupMemberCommandRepository.findByGroupIdAndMemberId(
            groupId,
            member.getId());
        // 예외 발생: http 메서드를 요청한 유저가 해당 그룹의 그룹원이 아님 - 403 NOT_GROUP_MEMBER
        if (groupMemberOptional.isEmpty()) {
            throw new NotGroupUserException(GroupErrorCode.NOT_GROUP_MEMBER);
        }
        GroupMember groupMember = groupMemberOptional.get();

        // 예외 발생: http 메서드를 요청한 유저가 해당 group의 그룹장이 아닌 경우 - NOT_GROUP_LEADER
        if (!groupMember.getRole().equals(GroupRole.GROUP_LEADER)) {
            throw new NotGroupLeaderException(GroupErrorCode.NOT_GROUP_LEADER);
        }

        // 예외 발생: 추방하려는 유저가 db에 없는 경우 - USER_NOT_FOUND
        Optional<Member> targetMemberOptional = memberRepository.findById(userId);
        if (targetMemberOptional.isEmpty()) {
            throw new UserNotFoundException(GroupErrorCode.USER_NOT_FOUND);
        }

        Optional<GroupMember> targetGroupMemberOptional = groupMemberCommandRepository.findByGroupIdAndMemberId(
            groupId,
            userId);
        // 예외 발생: 내보내려는 유저가 해당 그룹의 그룹원이 아님 - 403 NOT_GROUP_MEMBER
        if (targetGroupMemberOptional.isEmpty()) {
            throw new NotGroupUserException(GroupErrorCode.NOT_GROUP_MEMBER);
        }
        GroupMember targetGroupMember = targetGroupMemberOptional.get();
        // 예외 발생: 내보내려는 유저가 해당 그룹의 그룹장인 경우 - 409 USER_GROUP_LEADER
        if (!targetGroupMember.getRole().equals(GroupRole.GROUP_MEMBER)) {
            throw new UserGroupLeaderException(GroupErrorCode.USER_GROUP_LEADER);
        }

        //## 내보내기 진행
        // groupMember 삭제
        groupMemberCommandRepository.deleteByGroupAndMemberId(group,
            targetGroupMember.getMember().getId());
        // 그룹 내 이벤트에 대한 처리
        for (Event event : eventRepository.findByGroupId(groupId)) {
            // eventMember 삭제
            eventMemberRepository.deleteByEventAndMemberId(event,
                targetGroupMember.getMember().getId());
            // 이벤트 내 일정에 대한 처리
            for (Schedule schedule : scheduleCommandRepository.findByEvent(event)) {
                // scheduleMember 삭제
                // 일정에 본인이 일정 팀장인 경우 isRoleMaster를 true로 설정
                boolean isRoleMaster = false;
                if (scheduleMemberCommandRepository
                    .findByScheduleAndMemberId(schedule, targetGroupMember.getMember().getId())
                    .getRole()
                    .equals(ScheduleRole.ROLE_MASTER)) {
                    isRoleMaster = true;
                }
                scheduleMemberCommandRepository.deleteByScheduleAndMemberId(schedule,
                    targetGroupMember.getMember().getId());
                // 일정에 본인만 포함된 경우 -> schedule 삭제
                if (scheduleMemberCommandRepository.findBySchedule(schedule).isEmpty()) {
                    scheduleCommandRepository.delete(schedule);
                }
                // 일정에 본인이 일정 팀장인 경우 -> 팀장 권한 랜덤으로 넘기기
                else if (isRoleMaster) {
                    ArrayList<ScheduleMember> scheduleMembers = scheduleMemberCommandRepository.findBySchedule(
                        schedule);
                    // 일정 팀장 권한 한 번 전달 후 반복문 탈출
                    for (ScheduleMember scheduleMember1 : scheduleMembers) {
                        scheduleMember1.setRole(ScheduleRole.ROLE_MASTER);
                        break;
                    }
                }
            }
            // 이벤트에 본인만 포함된 경우 -> Event 삭제
            if (eventMemberRepository.findByEvent(event).isEmpty()) {
                eventRepository.delete(event);
            }
        }
    }


    // 그룹 멤버 권한 수정
    @Transactional
    public void modifyGroupRole(Long groupId, ControlGroupRoleRequest request) {
        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다.)

        Optional<Group> group = groupCommandRepository.findById(groupId);
        // 예외 발생: 해당 group은 존재하지 않음 - 404 GROUP_NOT_FOUND
        if (group.isEmpty()) {
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }

        // 예외 발생: 해당 group에 member가 존재하지 않음 - 404 USER_NOT_IN_GROUP
        // http 메서드 요청한 member
        Optional<GroupMember> groupMember = groupMemberCommandRepository.findByGroupIdAndMemberId(
            groupId, member.getId());
        if (groupMember.isEmpty()) {
            throw new UserNotInGroupException(GroupErrorCode.USER_NOT_IN_GROUP);
        }

        GroupRole groupRole = groupMember.get().getRole();
        // 예외 발생: member의 그룹 권한이 GroupLeader가 아닌 경우 - 403 NOT_GROUP_LEADER
        if (!groupRole.equals(GroupRole.GROUP_LEADER)) {
            throw new NotGroupLeaderException(GroupErrorCode.NOT_GROUP_LEADER);
        }

        // 예외 발생: request의 userId가 db에 없는 유저 - 404 USER_NOT_FOUND
        Optional<Member> memberOptional = memberRepository.findById(request.getUserId());
        if (memberOptional.isEmpty()) {
            throw new UserNotFoundException(GroupErrorCode.USER_NOT_FOUND);
        }

        Optional<GroupMember> groupMemberOptional = groupMemberCommandRepository.findByGroupIdAndMemberId(
            groupId,
            request.getUserId());
        // 예외 발생: request의 userId가 group에 없는 유저 - 404 USER_NOT_IN_GROUP
        if (groupMemberOptional.isEmpty()) {
            throw new UserNotInGroupException(GroupErrorCode.USER_NOT_IN_GROUP);
        }

        //## 권한 최신화
        // 만약 슈퍼방장이 강등된다면, 강등시킨 방장에게 슈퍼방장을 주자.
        GroupMember groupMember1 = groupMemberOptional.get();
        groupMember1.setRole(request.getGroupRole());
        if (request.getGroupRole().equals(GroupRole.GROUP_MEMBER) && groupMember1.getGroupAdmin()) {
            groupMember1.setGroupAdmin(false);
            groupMember.get().setGroupAdmin(true);
        }
        groupMemberCommandRepository.save(groupMember.get());
        groupMemberCommandRepository.save(groupMember1);
    }


    // 일회성 일정을 그룹으로 편입
    @Transactional
    public void transferSchedule(ScheduleToGroupRequest request) {

        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다: 401)

        Optional<Schedule> scheduleOptional = scheduleCommandRepository.findById(
            request.getScheduleId());
        // 예외 발생: 해당 schedule이 db에 없음: 404 SCHEDULE_NOT_FOUND
        if (scheduleOptional.isEmpty()) {
            throw new ScheduleNotFoundException(GroupErrorCode.SCHEDULE_NOT_FOUND);
        }

        Optional<Group> groupOptional = groupCommandRepository.findById(request.getGroupId());
        // 예외 발생: 해당 group은 존재하지 않음 - 404 GROUP_NOT_FOUND
        if (groupOptional.isEmpty()) {
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
        List<GroupMember> groupMembers = groupMemberCommandRepository.findByGroup(group);
        List<ScheduleMember> scheduleMembers = scheduleMemberCommandRepository.findByScheduleId(
            request.getScheduleId());
        for (ScheduleMember scheduleMember : scheduleMembers) {
            Member member1 = scheduleMember.getMember();
            boolean temp = false;
            for (GroupMember groupMember : groupMembers) {
                if (member1.equals(groupMember.getMember())) {
                    temp = true;
                    break;
                }
            }
            // 예외 발생: 해당 그룹으로 편입시킬 수 없음 404 - USER_NOT_IN_GROUP
            if (!temp) {
                throw new UserNotInGroupException(GroupErrorCode.USER_NOT_IN_GROUP);
            }
        }

        //## 편입
        for (GroupMember groupMember : groupMembers) {
            groupMemberCommandRepository.deleteByGroupAndMemberId(group1,
                groupMember.getMember().getId());
        }
        event.getGroup().getEvents().remove(event); // 역방향에서도 제거
        event.setGroup(null);                      // 주인 쪽에서도 제거
        // 2. 새 group과 연결
        event.setGroup(group);
        group.getEvents().add(event);
        groupCommandRepository.delete(group1);
    }


    // 그룹 탈퇴
    @Transactional
    public void withdrawGroup(Long groupId) {
        // http 요청 사용자 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) authentication.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        // TODO: member가 없다면 throw 예외(회원이 아닙니다: 401)

        Optional<Group> groupOptional = groupCommandRepository.findById(groupId);
        // 예외 발생: 해당 group은 존재하지 않음 - 404 GROUP_NOT_FOUND
        if (groupOptional.isEmpty()) {
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }
        Group group = groupOptional.get();

        Optional<GroupMember> groupMemberOptional = groupMemberCommandRepository.findByGroupIdAndMemberId(
            groupId,
            member.getId());
        // 예외 발생: http 메서드를 요청한 유저가 해당 그룹의 그룹원이 아님 - 403 NOT_GROUP_MEMBER
        if (groupMemberOptional.isEmpty()) {
            throw new NotGroupUserException(GroupErrorCode.NOT_GROUP_MEMBER);
        }
        GroupMember groupMember = groupMemberOptional.get();

        // 해당 그룹의 리더들 조회
        ArrayList<GroupMember> groupLeaders = groupMemberCommandRepository.findByGroupAndRole(group,
            GroupRole.GROUP_LEADER);
        ArrayList<GroupMember> groupMembers = groupMemberCommandRepository.findByGroupAndRole(group,
            GroupRole.GROUP_MEMBER);
        // 예외 발생: 해당 그룹의 리더가 1명이고 그룹원이 있는 경우 - 409 ONLY_ONE_LEADER
        if (groupMember.getRole().equals(GroupRole.GROUP_LEADER)
            && groupLeaders.size() == 1
            && !groupMembers.isEmpty()) {
            throw new OnlyOneGroupLeaderException(GroupErrorCode.ONE_GROUP_LEADER);
        }

        // 해당 그룹의 슈퍼 리더 이면서 그룹 내에 리더가 여러 명일 경우 -> 슈퍼 리더를 랜덤하게 타 groupLeader에게 이양
        if (groupMember.getRole().equals(GroupRole.GROUP_LEADER)
            && groupMember.getGroupAdmin()
            && groupLeaders.size() > 1) {
            // 그룹 슈러 리더 한 번 전달 후 반복문 탈출
            for (GroupMember groupLeader1 : groupLeaders) {
                if (groupLeader1.equals(groupMember)) {
                    continue;
                }
                groupLeader1.setGroupAdmin(true);
                break;
            }
        }

        //## 삭제 진행
        // groupMember 삭제
        groupMemberCommandRepository.deleteByGroupAndMemberId(group, member.getId());
        // 그룹 내 이벤트에 대한 처리
        for (Event event : eventRepository.findByGroupId(groupId)) {
            // eventMember 삭제
            eventMemberRepository.deleteByEventAndMemberId(event, member.getId());
            // 이벤트 내 일정에 대한 처리
            for (Schedule schedule : scheduleCommandRepository.findByEvent(event)) {
                // scheduleMember 삭제
                // 일정에 본인이 일정 팀장인 경우 isRoleMaster를 true로 설정
                boolean isRoleMaster = false;
                if (scheduleMemberCommandRepository
                    .findByScheduleAndMemberId(schedule, member.getId())
                    .getRole()
                    .equals(ScheduleRole.ROLE_MASTER)) {
                    isRoleMaster = true;
                }
                scheduleMemberCommandRepository.deleteByScheduleAndMemberId(schedule,
                    member.getId());
                // 일정에 본인만 포함된 경우 -> schedule 삭제
                if (scheduleMemberCommandRepository.findBySchedule(schedule).isEmpty()) {
                    scheduleCommandRepository.delete(schedule);
                }
                // 일정에 본인이 일정 팀장인 경우 -> 팀장 권한 랜덤으로 넘기기
                else if (isRoleMaster) {
                    ArrayList<ScheduleMember> scheduleMembers = scheduleMemberCommandRepository.findBySchedule(
                        schedule);
                    // 일정 팀장 권한 한 번 전달 후 반복문 탈출
                    for (ScheduleMember scheduleMember1 : scheduleMembers) {
                        scheduleMember1.setRole(ScheduleRole.ROLE_MASTER);
                        break;
                    }
                }
            }
            // 이벤트에 본인만 포함된 경우 -> Event 삭제
            if (eventMemberRepository.findByEvent(event).isEmpty()) {
                eventRepository.delete(event);
            }
        }
        // 그룹에 본인만 포함된 경우 -> group 삭제
        if (groupMemberCommandRepository.findByGroup(group).isEmpty()) {
            groupCommandRepository.delete(group);
        }
    }


}
