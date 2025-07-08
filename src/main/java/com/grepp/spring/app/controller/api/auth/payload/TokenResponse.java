package com.grepp.spring.app.controller.api.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String grantType;
    private Long expiresIn;
    private String refreshToken;
    private Long refreshExpiresIn;
    private String userId;
    private String userName;
}
