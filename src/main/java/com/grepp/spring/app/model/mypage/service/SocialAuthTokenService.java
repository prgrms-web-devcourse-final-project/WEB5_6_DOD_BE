package com.grepp.spring.app.model.mypage.service;

import com.grepp.spring.app.controller.api.auth.Provider;
import com.grepp.spring.app.controller.api.mypage.payload.response.GoogleTokenResponse;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.entity.SocialAuthToken;
import com.grepp.spring.app.model.member.repository.SocialAuthTokenRepository;
import com.grepp.spring.infra.error.exceptions.mypage.CalendarEventSaveFailedException;
import com.grepp.spring.infra.error.exceptions.mypage.TokenSaveFailedException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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

    try {
      socialAuthTokenRepository.save(token);
    } catch (DataAccessException e) {
      // DB 저장 실패 시만 예외 감싸기
      throw new CalendarEventSaveFailedException(MyPageErrorCode.CALENDAR_SYNC_FAILED);
    }
  }

  // - refresh_token으로 유효한 access_token 확보
  // - access_token 만료되면 refresh_token으로 재발급
  // - refresh_token 무효 → null 반환

  public String getValidAccessToken(SocialAuthToken token) {

    // 아직 access_token이 유효하면 그대로 반환
    if (token.getExpiresAt().isAfter(LocalDateTime.now())) {
      return token.getAccessToken();
    }


    // 토큰 만료 시 GoogleOAuthService 에서 재발급
    GoogleTokenResponse newToken = googleOAuthService.refreshAccessToken(token.getRefreshToken());

    // 새 access_token 이 정상적으로 왔으면 db 업데이트 -> 해당 예외 googleOAuthService 에서 처리
    // 엔티티 객체의 값만 먼저 바꿔놓기 (db save 이전)
    token.setAccessToken(newToken.getAccessToken());
    // 새로 발급받은 토큰의 만료 시각 다시 계산해서 저장함 (구글 api 가 주는 GoogleTokenResponse 에 있는 ExpiresIn)
    token.setExpiresAt(LocalDateTime.now().plusSeconds(newToken.getExpiresIn()));

      try {
        // db 저장 실패만 예외 처리
        socialAuthTokenRepository.save(token);
      } catch (DataAccessException e) {
        throw new TokenSaveFailedException(MyPageErrorCode.TOKEN_SAVE_FAILED);
      }
      return newToken.getAccessToken();
    }
}



