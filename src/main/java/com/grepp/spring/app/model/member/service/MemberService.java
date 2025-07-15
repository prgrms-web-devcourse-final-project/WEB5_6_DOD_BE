package com.grepp.spring.app.model.member.service;

import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.group.repository.GroupRepository;
import com.grepp.spring.app.controller.api.member.payload.ModifyMemberInfoResponse;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberRepository;
import com.grepp.spring.infra.error.exceptions.member.WithdrawNotAllowedException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final ScheduleMemberRepository scheduleMemberRepository;
    private final ScheduleCommandRepository scheduleCommandRepository;

    public Optional<Member> findById(String userId) {
        return memberRepository.findById(userId);
    }

    @Transactional
    public void withdraw(String userId, HttpServletResponse response) {
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
                    log.info("그룹 {}의 유일한 멤버이므로 그룹이 삭제됩니다.", group.getName());
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
                    log.info("그룹 {}의 새 관리자가 {} 님 에게 위임되었습니다.", group.getName(), newAdmin.getMember().getName());
                }else {
                    // 리더가 없으면 예외 발생 시켜야하니깐 그룹을 리스트에 추가해두자. 나중에 응답에 쓸거임
                    withdrawNotAllowedGroups.add(group);
                    log.info("위임할 리더가 없는 그룹: {}", group.getName());
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
                    log.info("일정 {}의 마지막 멤버이므로 일정이 삭제됩니다.", schedule.getScheduleName());
                } else {
                    // 다른 멤바가 있다면 랜덤으로 관리자 위임
                    ScheduleMember newScheduleMaster = selectRandomMember(scheduleMembers);
                    newScheduleMaster.setRole(ScheduleRole.ROLE_MASTER); // 새 관리자로 임명
                    scheduleMemberRepository.save(newScheduleMaster);
                    log.info("일정 {}의 새 관리자가 {}님 에게 위임되었습니다.", schedule.getScheduleName(),newScheduleMaster.getMember().getName());
                }
            }
        }

        // 이제 진짜 탈퇴.
        memberRepository.delete(member);
        log.info("회원 탈퇴가 완료되었습니다. 회원 ID: {}, 회원명: {}", member.getId(), member.getName());

        ResponseCookie deleteAccessToken = ResponseCookie.from("ACCESS_TOKEN", "")
            .path("/")
            .httpOnly(true)
//            .secure(true) // HTTPS 환경이라면 true
            .maxAge(0)
            .build();

        ResponseCookie deleteRefreshToken = ResponseCookie.from("REFRESH_TOKEN", "")
            .path("/")
            .httpOnly(true)
//            .secure(true) // HTTPS 환경이라면 true
            .maxAge(0)
            .build();

        // 응답 헤더에 쿠키 삭제 정보 추가
        response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshToken.toString());
        log.info("탈퇴한 회원이므로 자동으로 로그아웃됩니다.");
    }

    // 다음 리더를 뽑아주는 제네릭 메서드 : 이렇게 하면 그룹이랑 스케줄에서 같이 쓸 수 있습니다
    private <T> T selectRandomMember(List<T> members) {
        Random random = new Random();
        int randomIndex = random.nextInt(members.size());
        return members.get(randomIndex);
    }
    @Transactional
    public ModifyMemberInfoResponse modifyMemberInfo(String userId, String username) {

        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (StringUtils.hasText(username)) {
            // 앞 뒤 공백 제거
            username = username.trim();
            // 정규표현식으로 검증
            validateName(username);
            member.setName(username);
        }
        member.setProfileImageNumber((long) new Random().nextInt(10));

        memberRepository.save(member);
        log.info("프로필이 변경되었습니다. 이름: {}, 프로필 사진 번호: {}", member.getName(), member.getProfileImageNumber());

        // 변경된 사용자 정보 리턴 (나중에 응답에 넣을 거임)
        return new ModifyMemberInfoResponse(member.getId(), member.getName(), member.getProfileImageNumber());
    }

    // 이름 요구사항 검증 메서드
    private void validateName(String username){
        if (username == null) {
            throw new IllegalArgumentException("이름은 필수 입력값입니다.");
        }

        if (username.length() < 2 || username.length() > 10) {
            throw new IllegalArgumentException("이름은 2자 이상 10자 이하로만 가능합니다.");
        }
        // 한글, 영어 조합. 양 끝 제외 공백 허용
        String pattern = "^[가-힣a-zA-Z](?:[가-힣a-zA-Z ]*[가-힣a-zA-Z])?$";

        if (!username.matches(pattern)) {
            throw new IllegalArgumentException("이름은 한글, 영문만 사용 가능하며, 숫자나 특수문자는 포함할 수 없습니다.");
        }
    }

}
