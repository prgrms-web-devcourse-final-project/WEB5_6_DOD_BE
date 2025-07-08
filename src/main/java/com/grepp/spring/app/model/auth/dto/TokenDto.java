package com.grepp.spring.app.model.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private String atId;
    private String grantType;
    private Long expiresIn;
    private Long refreshExpiresIn;
    private String userId;
    private String userName;
}
