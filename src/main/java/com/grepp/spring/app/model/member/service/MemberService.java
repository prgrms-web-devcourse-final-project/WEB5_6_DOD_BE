package com.grepp.spring.app.model.member.service;

import com.grepp.spring.app.controller.api.member.payload.MemberInfoResponse;
import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
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
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.error.exceptions.member.WithdrawNotAllowedException;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final ScheduleMemberRepository scheduleMemberRepository;
    private final ScheduleCommandRepository scheduleCommandRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public Optional<Member> findById(String userId) {
        return memberRepository.findById(userId);
    }

    // 읽기 전용이니까 readOnly
    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberInfoResponse(String userId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND)); // 서윤님이 만든 예외 쓰기. 나중에 전역 처리 해야함

        return MemberInfoResponse.from(member);
    }

    @Transactional
    public void withdraw(String userId, HttpServletResponse response, HttpServletRequest request) {
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

        // 쿠키에서 토큰 제거
        ResponseCookie deleteAccessTokenCookie = TokenCookieFactory.createExpiredToken(AuthToken.ACCESS_TOKEN.name());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessTokenCookie.toString());

        ResponseCookie deleteRefreshTokenCookie = TokenCookieFactory.createExpiredToken(AuthToken.REFRESH_TOKEN.name());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshTokenCookie.toString());

        ResponseCookie deleteSessionIdCookie = TokenCookieFactory.createExpiredToken(AuthToken.AUTH_SERVER_SESSION_ID.name());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteSessionIdCookie.toString());

        // Redis에서 리프레시 토큰 제거
        String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            String atJti = jwtTokenProvider.getClaims(accessToken).getId();
            refreshTokenService.deleteByAccessTokenId(atJti);
        }

        SecurityContextHolder.clearContext();
        log.info("탈퇴한 회원이므로 자동으로 로그아웃됩니다.");
    }

    // 다음 리더를 뽑아주는 제네릭 메서드 : 이렇게 하면 그룹이랑 스케줄에서 같이 쓸 수 있습니다
    private <T> T selectRandomMember(List<T> members) {
        Random random = new Random();
        int randomIndex = random.nextInt(members.size());
        return members.get(randomIndex);
    }

    // 이름 변경
    @Transactional
    public ModifyMemberInfoResponse modifyMemberName(String userId, String username) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));

        // 이제 Member 엔티티에서 자체적으로 이름 변경 및 검증 처리
        member.updateName(username);
        memberRepository.save(member);
        log.info("이름이 변경되었습니다. 이름: {}", member.getName());

        // 변경된 사용자 정보 리턴
        return new ModifyMemberInfoResponse(member.getId(), member.getName(), member.getProfileImageNumber());
    }

    // 프로필 사진 변경
    @Transactional
    public ModifyMemberInfoResponse modifyProfileImage(String userId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));

        // Member 엔티티 자체에서 프로필 이미지 변경을 처리
        member.updateProfileImage();
        memberRepository.save(member);
        log.info("프로필 이미지가 변경되었습니다. 새로운 이미지 번호: {}", member.getProfileImageNumber());

        return new ModifyMemberInfoResponse(member.getId(), member.getName(), member.getProfileImageNumber());
    }
}
