package com.grepp.spring.app.model.mypage.service;


import com.grepp.spring.app.controller.api.auth.Provider;
import com.grepp.spring.app.controller.api.mypage.payload.response.GoogleTokenResponse;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.entity.SocialAuthToken;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.member.repository.SocialAuthTokenRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class SocialAuthTokenService {

  private final SocialAuthTokenRepository socialAuthTokenRepository;
  private final GoogleOAuthService googleOAuthService;


   // 최초 연동 시 토큰 저장 (있으면 업데이트)

  public void saveGoogleToken(Member member, GoogleTokenResponse tokenResponse) {
    SocialAuthToken token = socialAuthTokenRepository.findByMember(member)
        .orElse(new SocialAuthToken());

    token.setMember(member);
    token.setAccessToken(tokenResponse.getAccessToken());
    token.setRefreshToken(tokenResponse.getRefreshToken());
    token.setTokenType(tokenResponse.getTokenType());
    token.setExpiresAt(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));
    token.setProvider(Provider.GOOGLE);

    socialAuthTokenRepository.save(token);
  }


   // - refresh_token으로 유효한 access_token 확보
   // - access_token 만료되면 refresh_token으로 재발급
   // - refresh_token 무효 → null 반환

  public String getValidAccessToken(SocialAuthToken token) {
    // 아직 access_token이 유효하면 그대로 반환
    if (token.getExpiresAt().isAfter(LocalDateTime.now())) {
      return token.getAccessToken();
    }

    try {
      // refresh_token으로 새 access_token 발급
      GoogleTokenResponse newToken = googleOAuthService.refreshAccessToken(token.getRefreshToken());
      if (newToken == null || newToken.getAccessToken() == null) {
        return null; // refresh_token 무효 → 재인증 필요
      }

      // DB 업데이트
      token.setAccessToken(newToken.getAccessToken());
      token.setExpiresAt(LocalDateTime.now().plusSeconds(newToken.getExpiresIn()));
      socialAuthTokenRepository.save(token);

      return newToken.getAccessToken();
    } catch (HttpClientErrorException e) {
      return null; // refresh_token도 만료 → 재인증 필요
    }
  }
}


