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

@Service
@RequiredArgsConstructor
public class SocialAuthTokenService {

  private final MemberRepository memberRepository;
  private final SocialAuthTokenRepository socialAuthTokenRepository;

  public void saveGoogleToken(GoogleTokenResponse tokenResponse, String memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

    SocialAuthToken token = new SocialAuthToken();
    token.setAccessToken(tokenResponse.getAccessToken());
    token.setRefreshToken(tokenResponse.getRefreshToken());
    token.setTokenType(tokenResponse.getTokenType());
    token.setExpiresAt(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));
    token.setProvider(Provider.GOOGLE);
    token.setMember(member);

    socialAuthTokenRepository.save(token);
  }

}
