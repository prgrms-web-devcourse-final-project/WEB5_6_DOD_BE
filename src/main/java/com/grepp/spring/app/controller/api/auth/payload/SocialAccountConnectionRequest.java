package com.grepp.spring.app.controller.api.auth.payload;

import com.grepp.spring.app.controller.api.auth.Provider;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SocialAccountConnectionRequest {

    private Provider provider;
    private String providerId;
    private String authorizationCode;
    private String accessToken;
    private String refreshToken;


}
