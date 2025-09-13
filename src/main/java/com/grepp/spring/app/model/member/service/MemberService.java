package com.grepp.spring.app.model.member.service;

import com.grepp.spring.app.controller.api.member.payload.MemberInfoResponse;
import com.grepp.spring.app.model.auth.AuthService;
import com.grepp.spring.app.controller.api.member.payload.ModifyMemberInfoResponse;
import com.grepp.spring.app.model.group.service.GroupCommandService;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.event.MemberWithdrawalEvent;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.service.ScheduleCommandService;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthService authService;
    private final ApplicationEventPublisher eventPublisher;

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

    // 회원 탈퇴
    @Transactional
    public void withdraw(String userId, HttpServletResponse response, HttpServletRequest request) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));

        // 1. 회원 탈퇴 이벤트 발행
        eventPublisher.publishEvent(new MemberWithdrawalEvent(this, member));

        // 2. 회원 탈퇴(삭제)
        memberRepository.delete(member);
        log.info("회원 탈퇴가 완료되었습니다. 회원 ID: {}, 회원명: {}", member.getId(), member.getName());
        // 3. 로그아웃 (토큰 무효화)
        authService.logout(request, response); // 다른 서비스 참조. 괜찮은가 ?
        SecurityContextHolder.clearContext();
    }
}
