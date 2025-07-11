package com.grepp.spring.infra.auth.oauth2.user;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.profile = (Map<String, Object>) this.kakaoAccount.get("profile");
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "KAKAO";
    }

    @Override
    public String getName() {
        return profile.get("nickname").toString();
    }

    @Override
    public String getEmail() {
        return kakaoAccount.get("email").toString();
    }
}
