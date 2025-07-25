package com.grepp.spring.app.controller.api.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateAccessTokenResponse {

    private String accessToken;
    private String refreshToken;

}
