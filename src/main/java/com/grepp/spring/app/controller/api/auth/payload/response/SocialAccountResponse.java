package com.grepp.spring.app.controller.api.auth.payload.response;

import com.grepp.spring.app.controller.api.auth.Provider;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocialAccountResponse {

    private String socialAuthTokensId;
    private Provider provider;

}
