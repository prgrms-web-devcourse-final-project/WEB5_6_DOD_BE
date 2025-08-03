package com.grepp.spring.app.model.schedule.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
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

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ZoomOAuthService {

    @Value("${zoom.client-id}")
    private String clientId;
    @Value("${zoom.client-secret}")
    private String clientSecret;
    @Value("${zoom.account-id}")
    private String accountId;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken() {
        String url = "https://zoom.us/oauth/token";
        HttpHeaders headers = createBasicAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "account_credentials");
        params.add("account_id", accountId);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<ZoomTokenResponse> response = restTemplate.postForEntity(url, request, ZoomTokenResponse.class);

        if (response.getBody() != null) {
            return response.getBody().getAccessToken();
        }
        throw new RuntimeException("Zoom 액세스 토큰을 발급받지 못했습니다.");
    }

    private HttpHeaders createBasicAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = clientId + ":" + clientSecret;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);
        return headers;
    }

    @Data
    private static class ZoomTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
    }
}