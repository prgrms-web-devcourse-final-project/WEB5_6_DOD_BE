package com.grepp.spring.app.controller.api.mypage;

import com.grepp.spring.app.controller.api.mypage.payload.request.SetCalendarSyncRequest;
import com.grepp.spring.app.controller.api.mypage.payload.response.GoogleTokenResponse;
import com.grepp.spring.app.model.auth.domain.Principal;
//import com.grepp.spring.app.model.mainpage.service.CalendarService;
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

@RestController
public class CalendarOAuthController {

  @Value("${google.calendar.client-id}")
  private String clientId;

  @Value("${google.calendar.client-secret}")
  private String clientSecret;

  @Value("${google.calendar.redirect-uri}")
  private String redirectUri;

//  @Autowired
//  private CalendarService calendarService;

  @GetMapping("/oauth2/callback/google-calendar")
  public String handleGoogleCalendarCallback(
      @RequestParam("code") String code,
      @AuthenticationPrincipal Principal principal
  ) {

//    if (principal == null) {
//      throw new AuthenticationCredentialsNotFoundException("로그인 필요");
//    }
//    GoogleTokenResponse token = exchangeCodeForToken(code);
//    String memberId = principal.getUsername();
//
//    SetCalendarSyncRequest request = new SetCalendarSyncRequest();
//    request.setSynced(true);
//    request.setAccessToken(token.getAccessToken());
//    request.setRefreshToken(token.getRefreshToken());
//
//    calendarService.updateSyncSetting(memberId, request);  // 실제로는 로그인된 사용자로 대체

    return "연동 성공!";
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
