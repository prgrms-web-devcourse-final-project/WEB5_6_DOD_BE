package com.grepp.spring.app.controller.api.mypage;

import com.grepp.spring.app.controller.api.mypage.payload.request.SetCalendarSyncRequest;
import com.grepp.spring.app.controller.api.mypage.payload.response.GoogleTokenResponse;
import com.grepp.spring.app.controller.api.mypage.payload.response.SetCalendarSyncResponse;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.mainpage.service.CalendarService;
import com.grepp.spring.app.model.mypage.service.CalendarSyncService;
import com.grepp.spring.app.model.mypage.service.SocialAuthTokenService;
import com.grepp.spring.infra.response.ApiResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController("/api/v1/calendar")
@RequiredArgsConstructor
public class CalendarOAuthController {

  private final SocialAuthTokenService socialAuthTokenService;

  @Value("${google.calendar.client-id}")
  private String clientId;

  @Value("${google.calendar.client-secret}")
  private String clientSecret;

  @Value("${google.calendar.redirect-uri}")
  private String redirectUri;

  @Autowired
  private CalendarService calendarService;
  @Autowired
  private CalendarSyncService calendarSyncService;

  @GetMapping("/oauth2/callback/google-calendar")
  public ApiResponse<SetCalendarSyncResponse> handleGoogleCalendarCallback(
      @RequestParam("code") String code,
      @AuthenticationPrincipal Principal principal
  ) {

    if (principal == null) {
      throw new AuthenticationCredentialsNotFoundException("로그인 필요");
    }

    GoogleTokenResponse token = exchangeCodeForToken(code);
    String memberId = principal.getUsername();

    // 소셜 토큰 저장
    socialAuthTokenService.saveGoogleToken(token, memberId);

    SetCalendarSyncRequest request = new SetCalendarSyncRequest();
    request.setSynced(true);
    request.setAccessToken(token.getAccessToken());
    request.setRefreshToken(token.getRefreshToken());

    calendarSyncService.updateSyncSetting(memberId, request);  // 실제로는 로그인된 사용자로 대체

    SetCalendarSyncResponse response = new SetCalendarSyncResponse(true, LocalDateTime.now());

    return ApiResponse.success(response); //
  }

  private GoogleTokenResponse exchangeCodeForToken(String code) {
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", code);
    params.add("client_id", clientId);
    params.add("client_secret", clientSecret);
    params.add("redirect_uri", redirectUri);
    params.add("grant_type", "authorization_code");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(
        "https://oauth2.googleapis.com/token",
        request,
        GoogleTokenResponse.class
    );

    return response.getBody();
  }
}
