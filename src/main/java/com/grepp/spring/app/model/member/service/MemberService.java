package com.grepp.spring.app.model.member.service;

import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.group.repository.GroupRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberRepository;
import com.grepp.spring.infra.error.exceptions.member.WithdrawNotAllowedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final ScheduleMemberRepository scheduleMemberRepository;
    private final ScheduleCommandRepository scheduleCommandRepository;

    public Optional<Member> findById(String userId) {
        return memberRepository.findById(userId);
    }

    @Transactional
    public void withdraw(String userId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        // 이 사람이 그룹의 관리자인지를 체크
        // 관리자라면 이 사람이 관리자인 그룹을 전부 조회

        // 본인이 관리자인 그룹 조회
        List<Group> adminGroups = groupMemberRepository.findGroupsByMemberAndAdmin(member);
        // 예외처리에 사용할 수퍼 리더를 넘길 수 없는 그룹을 저장할 리스트
        List<Group> withdrawNotAllowedGroups = new ArrayList<>();
        // 본인이 수퍼 리더(Admin)인 그룹이 있다면, 각 그룹 별 리더 중 랜덤으로 권한 넘기기
        if (!adminGroups.isEmpty()){
            for (Group group : adminGroups){
                // 각 그룹의 모든 멤바 조회
                List<GroupMember> groupMembers = groupMemberRepository.findByGroup(group);
                // 내가 그룹의 유일한 멤바라면 그룹까지 날려버리깅
                if (groupMembers.size() == 1 && groupMembers.getFirst().getMember().equals(member)){
                    groupRepository.delete(group);
                    log.info("그룹 " + group.getName() + "의 유일한 멤버이므로 그룹이 삭제됩니다.");
                    continue;
                }

                // 각 그룹의 모든 리더 조회 (나 빼고)
                List<GroupMember> groupLeaders = groupMemberRepository.findByGroupAndLeaderAndMemberNot(group, member);
                // 본인 외 다른 멤바가 있다면?
                if (!groupLeaders.isEmpty()){
                    // 리더가 존재한다면 랜덤으로 관리자 넘겨버리깅
                    GroupMember newAdmin = selectRandomMember(groupLeaders);

                    newAdmin.setGroupAdmin(true); // 너 이제부터 수퍼리더야.
                    groupMemberRepository.save(newAdmin);
                    log.info("그룹 " + group.getName() + "의 새 관리자가 " + newAdmin.getMember().getName() + "님 으로 위임되었습니다.");
                }else {
                    // 리더가 없으면 예외 발생 시켜야하니깐 그룹을 리스트에 추가해두자. 나중에 응답에 쓸거임
                    withdrawNotAllowedGroups.add(group);
                    log.info("위임할 리더가 없는 그룹: " + group.getName());
                }
            }
        }
        // 수퍼 리더 위임이 불가능한 그룹이 있다면 예외처리
        // 탈퇴 전에 그룹에 리더를 만들어야 해요~~!!
        if (!withdrawNotAllowedGroups.isEmpty()){
            throw new WithdrawNotAllowedException("이 그룹에 관리자 권한을 위임할 수 있는 리더를 추가해주세요.", withdrawNotAllowedGroups);
        }

        // 일정 관리자 처리
        // 본인이 일정 마스터인 모든 일정 조회
        List<Schedule> masterSchedules = scheduleMemberRepository.findByMember(member);

        // 본인이 마스터인 일정이 있다면,
        if (!masterSchedules.isEmpty()){
            for (Schedule schedule : masterSchedules){
                // 각 일정 내 모든 멤바 조회 (나 빼고)
                List<ScheduleMember> scheduleMembers = scheduleMemberRepository.findByScheduleAndMemberNot(schedule, member);

                if (scheduleMembers.isEmpty()){
                    // 본인이 일정의 유일 멤버라면? 일정 너도 삭제야.
                    scheduleCommandRepository.delete(schedule);
                    log.info("일정 " + schedule.getScheduleName() + "의 마지막 멤버이므로 일정이 삭제됩니다.");
                } else {
                    // 다른 멤바가 있다면 랜덤으로 관리자 위임
                    ScheduleMember newScheduleMaster = selectRandomMember(scheduleMembers);
                    newScheduleMaster.setRole(ScheduleRole.ROLE_MASTER); // 새 관리자로 임명
                    scheduleMemberRepository.save(newScheduleMaster);
                    log.info("일정 "+ schedule.getScheduleName() +"의 새 관리자가 " + newScheduleMaster.getMember().getName() + " 님 으로 위임되었습니다.");
                }
            }
        }

        // 연관 테이블에서 삭제 먼저 (양방향 매핑이 아니라서 수동으로..)
        groupMemberRepository.deleteByMember(member);
        scheduleMemberRepository.deleteByMember(member);
        // 이제 진짜 탈퇴.
        memberRepository.delete(member);
        log.info("회원 탈퇴가 완료되었습니다. 회원 ID: " + member.getId(), "회원명: ", member.getName());
    }

    // 다음 리더를 뽑아주는 제네릭 메서드 : 이렇게 하면 그룹이랑 스케줄에서 같이 쓸 수 있습니다
    private <T> T selectRandomMember(List<T> members) {
        Random random = new Random();
        int randomIndex = random.nextInt(members.size());
        return members.get(randomIndex);
    }
}
