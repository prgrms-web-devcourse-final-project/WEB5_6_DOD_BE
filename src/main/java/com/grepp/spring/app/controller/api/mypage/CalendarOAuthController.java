package com.grepp.spring.app.controller.api.mypage;

import com.grepp.spring.app.controller.api.mypage.payload.response.GoogleTokenResponse;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.service.GoogleOAuthService;
import com.grepp.spring.app.model.mypage.service.SocialAuthTokenService;
import com.grepp.spring.infra.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping("/oauth2/callback")
@RequiredArgsConstructor
public class CalendarOAuthController {

  private final MemberRepository memberRepository;
  private final SocialAuthTokenService socialAuthTokenService;
  private final GoogleOAuthService googleOAuthService;

  // 구글 캘린더 OAuth 콜백 (최초 인증)
  @GetMapping("/google-calendar")
  public ApiResponse<Void> handleGoogleCalendarCallback(@RequestParam("code") String code) {

    // 로그인 사용자 정보 가져오기
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new AuthenticationCredentialsNotFoundException("로그인 필요");
    }

    String memberId = authentication.getName();

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

    log.info("✅ [CALLBACK] 구글 캘린더 OAuth 콜백: code={}", code);

    // code → access_token + refresh_token 교환
    GoogleTokenResponse token = googleOAuthService.exchangeCodeForToken(code);

    // DB 저장 (있으면 업데이트)
    socialAuthTokenService.saveGoogleToken(member, token);

    log.info("[CALLBACK] 구글 캘린더 토큰 저장 완료 for member={}", memberId);

    return ApiResponse.success(null); // 새로고침 API가 따로 있으므로 단순 성공만 반환
  }
}

