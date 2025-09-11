package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.group.payload.request.ControlGroupRoleRequest;
import com.grepp.spring.app.controller.api.group.payload.request.CreateGroupRequest;
import com.grepp.spring.app.controller.api.group.payload.request.ModifyGroupInfoRequest;
import com.grepp.spring.app.controller.api.group.payload.request.ScheduleToGroupRequest;
import com.grepp.spring.app.controller.api.group.payload.response.CreateGroupResponse;
import com.grepp.spring.app.controller.api.group.payload.response.InviteGroupMemberResponse;
import com.grepp.spring.app.controller.api.group.payload.response.ModifyGroupInfoResponse;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.event.MemberWithdrawalEvent;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberCommandRepository;
import com.grepp.spring.infra.utils.RandomPicker;
import com.grepp.spring.infra.error.exceptions.group.GroupAuthenticationException;
import com.grepp.spring.infra.error.exceptions.group.GroupNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.NotGroupUserException;
import com.grepp.spring.infra.error.exceptions.group.OnlyOneGroupLeaderException;
import com.grepp.spring.infra.error.exceptions.group.ScheduleNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.UserAlreadyInGroupException;
import com.grepp.spring.infra.error.exceptions.group.UserNotFoundException;
import com.grepp.spring.infra.error.exceptions.member.WithdrawNotAllowedException;
import com.grepp.spring.infra.response.GroupErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
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
    private final GroupMemberRepository groupMemberRepository;

    // member를 가져오는 전략
    // 필드 내의 Member 객체의 id에만 접근 할 경우 select문을 db에 날리지 않고도 member조회 가능
    // memberRepository의 getReferenceById 메서드 활용

    // 그룹 생성
    @Transactional
    public CreateGroupResponse registGroup(CreateGroupRequest request) {
        // http 요청 사용자 조회 - 401 AUTHENTICATED_REQUIRED 예외 처리
        Member member = findHttpRequestMemberOrThrow();

        // 로직 시작
        //## 그룹 생성
        Group group = Group.createGroup(request);
        groupCommandRepository.save(group);
        // 그룹-멤버 생성 (중간 테이블)
        GroupMember groupMember = GroupMember.createGroupMemberLeader(group, member);
        groupMemberCommandRepository.save(groupMember);
        // 그룹 생성 후 정보 반환
        return CreateGroupResponse.createCreateGroupResponse(group);
    }


    // 그룹 멤버 추가
    @Transactional
    public InviteGroupMemberResponse addGroupMember(Long groupId) {
        // http 요청 사용자 조회 - 401 AUTHENTICATED_REQUIRED 예외 처리
        Member member = findHttpRequestMemberOrThrow();
        // 그룹 조회 - 404 GROUP_NOT_FOUND 예외 처리
        Group group = findGroupOrThrow(groupId);
        // 그룹 멤버 조회 - 409 USER_ALREADY_IN_GROUP 예외 처리
        isMemberInGroupThenThrow(groupId, member.getId());

        // 로직 시작
        //## 그룹에 멤버 추가
        // 그룹-멤버 생성 (중간 테이블)
        GroupMember groupMember = GroupMember.createGroupMemberMember(group, member);
        groupMemberCommandRepository.save(groupMember);
        // 그룹 수정 후 정보 반환
        return InviteGroupMemberResponse.createInviteGroupMemberResponse(member, groupId);
    }


    // 그룹 삭제
    @Transactional
    public void deleteGroup(Long groupId) {
        // http 요청 사용자 조회 - 401 AUTHENTICATED_REQUIRED 예외 처리
        Member member = findHttpRequestMemberOrThrow();
        // 그룹 조회 - 404 GROUP_NOT_FOUND 예외 처리
        Group group = findGroupOrThrow(groupId);
        // 그룹멤버 조회 - 403 NOT_GROUP_MEMBER 예외 처리
        GroupMember groupMember = findGroupMemberOrThrow(groupId, member.getId());
        // 그룹멤버 리더 권한 조회 - 403 NOT_GROUP_LEADER 예외 처리
        groupMember.isGroupLeaderOrThrow();

        // 로직 시작
        //## 그룹 삭제 진행, cascade 삭제 진행
        groupCommandRepository.deleteById(groupId);
    }


    // 그룹 정보 수정
    @Transactional
    public ModifyGroupInfoResponse modifyGroup(Long groupId, ModifyGroupInfoRequest request) {
        // http 요청 사용자 조회 - 401 AUTHENTICATED_REQUIRED 예외 처리
        Member member = findHttpRequestMemberOrThrow();
        // 그룹 조회 - 404 GROUP_NOT_FOUND 예외 처리
        Group group = findGroupOrThrow(groupId);
        // 그룹멤버 조회 - 403 NOT_GROUP_MEMBER 예외 처리
        GroupMember groupMember = findGroupMemberOrThrow(groupId, member.getId());
        // 그룹멤버 리더 권한 조회 - 403 NOT_GROUP_LEADER 예외 처리
        groupMember.isGroupLeaderOrThrow();

        // 로직 시작
        //## patch 메서드 진행
        group.update(request);
        // 수정 사항 저장
        groupCommandRepository.save(group);
        return ModifyGroupInfoResponse.createModifyGroupInfoResponse(group);
    }


    // 멤버 추방하기
    @Transactional
    public void deportMember(Long groupId, String userId) {
        // http 요청 사용자 조회 - 401 AUTHENTICATED_REQUIRED 예외 처리
        Member member = findHttpRequestMemberOrThrow();
        // 그룹 조회 - 404 GROUP_NOT_FOUND 예외 처리
        Group group = findGroupOrThrow(groupId);
        // 멤버 조회 - 404 USER_NOT_FOUND 예외 처리 (추방하려는 대상)
        Member targetMember = findMemberOrThrow(userId);
        // 그룹멤버 조회 - 403 NOT_GROUP_MEMBER 예외 처리
        GroupMember groupMember = findGroupMemberOrThrow(groupId, member.getId());
        // 그룹멤버 조회 - 403 NOT_GROUP_MEMBER 예외 처리 (추방하려는 대상)
        GroupMember targetGroupMember = findGroupMemberOrThrow(groupId, targetMember.getId());
        // 그룹멤버 리더 권한 조회 - 403 NOT_GROUP_LEADER 예외 처리
        groupMember.isGroupLeaderOrThrow();
        // 그룹멤버 리더 권한 조회 - 409 USER_GROUP_LEADER 예외 처리 (추방하려는 대상)
        targetGroupMember.isNotGroupLeaderOrThrow();

        // 로직 시작
        //## 내보내기 진행
        // groupMember 삭제
        groupMemberCommandRepository.deleteByGroupAndMemberId(group,
            targetGroupMember.getMember().getId());
        // 그룹 내 이벤트에 대한 처리
        deportMemberInEvent(targetGroupMember, groupId);
    }


    // 그룹 멤버 권한 수정
    @Transactional
    public void modifyGroupRole(Long groupId, ControlGroupRoleRequest request) {
        // http 요청 사용자 조회 - 401 AUTHENTICATED_REQUIRED 예외 처리
        Member member = findHttpRequestMemberOrThrow();
        // 그룹 조회 - 404 GROUP_NOT_FOUND 예외 처리
        Group group = findGroupOrThrow(groupId);
        // 멤버 조회 - 404 USER_NOT_FOUND 예외 처리 (권한 수정 대상)
        Member targetMember = findMemberOrThrow(request.getUserId());
        // 그룹멤버 조회 - 403 NOT_GROUP_MEMBER 예외 처리
        GroupMember groupMember = findGroupMemberOrThrow(groupId, member.getId());
        // 그룹멤버 조회 - 403 NOT_GROUP_MEMBER 예외 처리 (권한 수정 대상)
        GroupMember targetGroupMember = findGroupMemberOrThrow(groupId, targetMember.getId());
        // 그룹멤버 리더 권한 조회 - 403 NOT_GROUP_LEADER 예외 처리
        groupMember.isGroupLeaderOrThrow();

        // 로직 시작
        //## 권한 최신화
        targetGroupMember.updateGroupRole(request);
        // 만약 슈퍼방장이 강등된다면, 강등시킨 방장에게 슈퍼방장을 주자.
        targetGroupMember.delegateAdmin(groupMember);
        groupMemberCommandRepository.save(groupMember);
        groupMemberCommandRepository.save(targetGroupMember);
    }


    // 일회성 일정을 그룹으로 편입
    @Transactional
    public void transferSchedule(ScheduleToGroupRequest request) {
        // http 요청 사용자 조회 - 401 AUTHENTICATED_REQUIRED 예외 처리
        Member member = findHttpRequestMemberOrThrow();
        // 그룹 조회 - 404 GROUP_NOT_FOUND 예외 처리
        Group group = findGroupOrThrow(request.getGroupId());
        // 일정 조회 - 404 SCHEDULE_NOT_FOUND 예외 처리
        Schedule schedule = findScheduleOrThrow(request.getScheduleId());

        Event event = schedule.getEvent();
        Group group1 = event.getGroup();
        // 일정 일회성 조회 - 409 SCHEDULE_ALREADY_IN_GROUP
        group1.isNotInGroupOrThrow();

        // 스케쥴 멤버 조회 - 403 NOT_GROUP_MEMBER 예외 처리
        findScheduleMembersOrThrow(request);

        // 로직 시작
        //## 편입
        transferScheduleToGroup(group1, group, event);
    }


    // 그룹 탈퇴
    @Transactional
    public void withdrawGroup(Long groupId) {
        // http 요청 사용자 조회 - 401 AUTHENTICATED_REQUIRED 예외 처리
        Member member = findHttpRequestMemberOrThrow();
        // 그룹 조회 - 404 GROUP_NOT_FOUND 예외 처리
        Group group = findGroupOrThrow(groupId);
        // 그룹멤버 조회 - 403 NOT_GROUP_MEMBER 예외 처리
        GroupMember groupMember = findGroupMemberOrThrow(groupId, member.getId());
        // 그룹멤버 권한 조회 - 409 ONE_GROUP_LEADER
        isNotOnlyGroupLeaderWithGroupMembersOrThrow(group, groupMember);

        // 로직 시작
        //## 삭제 진행
        // 해당 그룹의 슈퍼 리더 이면서 그룹 내에 리더가 여러 명일 경우 -> 슈퍼 리더를 랜덤하게 타 groupLeader에게 이양
        delegateAdmin(group, groupMember);
        // groupMember 삭제
        groupMemberCommandRepository.deleteByGroupAndMemberId(group, member.getId());
        // 그룹 내 이벤트에 대한 처리
        deportMemberInEvent(groupMember, groupId);
        // 그룹에 본인만 포함된 경우 -> group 삭제
        if (groupMemberCommandRepository.findByGroup(group).isEmpty()) {
            groupCommandRepository.delete(group);
        }
    }


    /// 범용적인 예외처리 메서드
    // http 메서드 요청한 member 조회 - 401 AUTHENTICATION_REQUIRED 예외 처리
    protected Member findHttpRequestMemberOrThrow() {
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


    // 멤버 조회 - 404 USER_NOT_FOUND 예외 처리
    private Member findMemberOrThrow(String userId) {
        Optional<Member> memberOptional = memberRepository.findById(userId);
        if (memberOptional.isEmpty()) {
            throw new UserNotFoundException(GroupErrorCode.USER_NOT_FOUND);
        }
        return memberOptional.get();
    }


    // 그룹 조회 - 404 GROUP_NOT_FOUND 예외 처리
    private Group findGroupOrThrow(Long groupId) {
        Optional<Group> groupOptional = groupCommandRepository.findById(groupId);
        if (groupOptional.isEmpty()) {
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }
        return groupOptional.get();
    }


    // 일정 조회 - 404 SCHEDULE_NOT_FOUND 예외 처리
    private Schedule findScheduleOrThrow(Long scheduleId) {
        Optional<Schedule> scheduleOptional = scheduleCommandRepository.findById(
            scheduleId);
        if (scheduleOptional.isEmpty()) {
            throw new ScheduleNotFoundException(GroupErrorCode.SCHEDULE_NOT_FOUND);
        }
        return scheduleOptional.get();
    }


    // 그룹멤버 조회 - 403 NOT_GROUP_MEMBER 예외 처리
    private GroupMember findGroupMemberOrThrow(Long groupId, String id) {
        Optional<GroupMember> groupMemberOptional = groupMemberCommandRepository.findByGroupIdAndMemberId(
            groupId, id);
        if (groupMemberOptional.isEmpty()) {
            throw new NotGroupUserException(GroupErrorCode.NOT_GROUP_MEMBER);
        }
        return groupMemberOptional.get();
    }


    // 그룹 멤버 조회 - 409 USER_ALREADY_IN_GROUP 예외 처리
    private void isMemberInGroupThenThrow(Long groupId, String id) {
        Optional<GroupMember> groupMemberOptional = groupMemberCommandRepository.findByGroupIdAndMemberId(
            groupId, id);
        if (groupMemberOptional.isPresent()) {
            throw new UserAlreadyInGroupException(GroupErrorCode.USER_ALREADY_IN_GROUP);
        }
    }


    // 그룹멤버 권한 조회 - 409 ONE_GROUP_LEADER
    private void isNotOnlyGroupLeaderWithGroupMembersOrThrow(Group group, GroupMember groupMember) {
        long groupLeaderNum = groupMemberCommandRepository.findByGroupAndRole(group,
            GroupRole.GROUP_LEADER).size();
        long groupMemberNum = groupMemberCommandRepository.findByGroupAndRole(group,
            GroupRole.GROUP_MEMBER).size();
        if (groupMember.isGroupLeader()
            && groupLeaderNum == 1
            && groupMemberNum != 0) {
            throw new OnlyOneGroupLeaderException(GroupErrorCode.ONE_GROUP_LEADER);
        }
    }


    ///  일반적인 Group Service 내용
    // group1에 속했던 event를 group으로 편입 및 group1은 삭제
    private void transferScheduleToGroup(Group group1, Group group, Event event) {
        for (GroupMember groupMember : groupMemberCommandRepository.findByGroup(group1)) {
            groupMember.setGroup(group);
        }
        group.getEvents().add(event);
        groupCommandRepository.delete(group1);
        // TODO: event 객체의 책임으로 위임해야 함
        changeGroup(event, group);
    }

    private void changeGroup(Event event, Group group) {
        event.getGroup().getEvents().remove(event); // 역방향에서도 제거
        event.setGroup(null);                      // 주인 쪽에서도 제거
        // 2. 새 group과 연결
        event.setGroup(group);
    }


    // 만약 탈퇴하려는 자의 groupAdmin이 true인 경우 groupAdmin 위임
    private void delegateAdmin(Group group, GroupMember groupMember) {
        ArrayList<GroupMember> groupLeaders = groupMemberCommandRepository.findByGroupAndRole(group,
            GroupRole.GROUP_LEADER);
        if (groupMember.isGroupLeader()) {
            for (GroupMember groupLeader1 : groupLeaders) {
                if (groupLeader1.equals(groupMember)) {
                    continue;
                }
                groupMember.delegateAdmin(groupLeader1);
                break;
            }
        }
    }


    /// Event Service 내용
    // event에서 추방
    private void deportMemberInEvent(GroupMember targetGroupMember, Long groupId) {
        for (Event event : eventRepository.findByGroupId(groupId)) {
            // eventMember 삭제
            if(!eventMemberRepository.existsByEventIdAndMemberId(event.getId(), targetGroupMember.getMember().getId())){
                continue;
            }
            eventMemberRepository.deleteByEventAndMemberId(event,
                targetGroupMember.getMember().getId());
            // schedule에서 추방
            deportMemberInSchedule(targetGroupMember, event);
            // 이벤트에 본인만 포함된 경우 -> Event 삭제
            if (eventMemberRepository.findByEvent(event).isEmpty()) {
                eventRepository.delete(event);
            }
        }
    }


    /// Schedule Service 내용
    // schedule에서 추방
    private void deportMemberInSchedule(GroupMember targetGroupMember, Event event) {
        // 이벤트 내 일정에 대한 처리
        for (Schedule schedule : scheduleCommandRepository.findByEvent(event)) {
            if(!scheduleMemberCommandRepository.existsByScheduleIdAndMemberId(schedule.getId(), targetGroupMember.getMember().getId())){
                continue;
            }
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
    }


    // 스케줄멤버들의 조회
    private void findScheduleMembersOrThrow(ScheduleToGroupRequest request) {
        List<ScheduleMember> scheduleMembers = scheduleMemberCommandRepository.findByScheduleId(
            request.getScheduleId());
        for (ScheduleMember scheduleMember : scheduleMembers) {
            Member member1 = scheduleMember.getMember();
            // 그룹멤버 조회 - 403 NOT_GROUP_MEMBER 예외 처리
            GroupMember groupMember = findGroupMemberOrThrow(request.getGroupId(), member1.getId());
        }
    }

    // 회원 탈퇴 이벤트 리스너
    @EventListener
    @Transactional
    public void handleMemberWithdrawal(MemberWithdrawalEvent event) {
        Member member = event.getMember();
        handleGroupWithdrawal(member);
    }

    // 회원 탈퇴 중 그룹 관련 처리 메서드
    @Transactional
    public void handleGroupWithdrawal(Member member) {
        // 본인이 관리자인 그룹 조회
        List<Group> adminGroups = groupMemberRepository.findGroupsByMemberAndAdmin(member);
        // 예외처리에 사용할 수퍼 리더를 넘길 수 없는 그룹을 저장할 리스트
        List<Group> withdrawNotAllowedGroups = new ArrayList<>();
        // 본인이 수퍼 리더(Admin)인 그룹이 있다면, 각 그룹 별 리더 중 랜덤으로 권한 넘기기
        if (!adminGroups.isEmpty()) {
            for (Group group : adminGroups) {
                // 각 그룹의 모든 멤바 조회
                List<GroupMember> groupMembersInGroup = groupMemberRepository.findByGroup(group);
                // 내가 그룹의 유일한 멤바라면 그룹까지 날려버리깅
                if (groupMembersInGroup.size() == 1 && groupMembersInGroup.getFirst().getMember()
                    .equals(member)) {
                    groupCommandRepository.delete(group);
                    log.info("그룹 {}의 유일한 멤버이므로 그룹이 삭제됩니다.", group.getName());
                    continue;
                }

                // 다른 리더가 있다면 랜덤으로 관리자 위임
                // 각 그룹의 모든 리더 조회 (나 빼고)
                List<GroupMember> otherLeaders = groupMemberRepository.findByGroupAndLeaderAndMemberNot(
                    group, member);
                // 본인 외 다른 멤바가 있다면?
                if (!otherLeaders.isEmpty()) {
                    // 리더가 존재한다면 랜덤으로 관리자 넘겨버리깅
                    GroupMember newAdmin = RandomPicker.pickRandom(otherLeaders);
                    newAdmin.grantAdminRole(); // 너 이제부터 수퍼리더야.
                    groupMemberRepository.save(newAdmin);
                    log.info("그룹 {}의 새 관리자가 {} 님 에게 위임되었습니다.", group.getName(),
                        newAdmin.getMember().getName());
                } else {
                    // 리더가 없으면 예외 발생 시켜야하니깐 그룹을 리스트에 추가해두자. 나중에 응답에 쓸거임
                    withdrawNotAllowedGroups.add(group);
                    log.warn("위임할 리더가 없는 그룹: {}", group.getName());
                }
            }
        }
        // 수퍼 리더 위임이 불가능한 그룹이 있다면 예외처리
        if (!withdrawNotAllowedGroups.isEmpty()) {
            throw new WithdrawNotAllowedException("이 그룹에 관리자 권한을 위임할 수 있는 리더를 추가해주세요.",
                withdrawNotAllowedGroups);
        }
    }

}
