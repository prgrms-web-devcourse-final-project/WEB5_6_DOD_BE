package com.grepp.spring.app.model.mypage.service;

import com.grepp.spring.app.controller.api.mypage.payload.response.GoogleTokenResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

  @Value("${google.calendar.client-id}")
  private String clientId;

  @Value("${google.calendar.client-secret}")
  private String clientSecret;

  @Value("${google.calendar.redirect-uri}")
  private String redirectUri;

  private final RestTemplate restTemplate = new RestTemplate();


   // 구글 OAuth 최초 연동/재인증 URL 생성

  public String buildReauthUrl() {
    return UriComponentsBuilder.fromUri(URI.create("https://accounts.google.com/o/oauth2/v2/auth"))
        .queryParam("client_id", clientId)
        .queryParam("redirect_uri", redirectUri)
        .queryParam("response_type", "code")
        .queryParam("scope", "https://www.googleapis.com/auth/calendar.readonly")
        .queryParam("access_type", "offline")   // refresh_token 받기 위해 필요
        .queryParam("prompt", "consent")        // 매번 refresh_token 강제 발급하려면 필요
        .build()
        .toUriString();
  }


   // 최초 연동 시: authorization_code → access_token + refresh_token 교환

  public GoogleTokenResponse exchangeCodeForToken(String code) {
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

    return response.getBody(); // 여기엔 access_token + refresh_token 둘 다 옴
  }


   // 기존 refresh_token으로 새 access_token만 재발급

  public GoogleTokenResponse refreshAccessToken(String refreshToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("client_id", clientId);
    params.add("client_secret", clientSecret);
    params.add("refresh_token", refreshToken);
    params.add("grant_type", "refresh_token");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(
        "https://oauth2.googleapis.com/token",
        request,
        GoogleTokenResponse.class
    );

    return response.getBody(); // 여기엔 refresh_token은 안 내려오고 access_token만 옴
  }
}
