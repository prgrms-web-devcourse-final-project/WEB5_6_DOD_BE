package com.grepp.spring.infra.auth.oauth2.user;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserInfo {
    
    String getProviderId();
    String getProvider();
    String getName();
    String getEmail();

    static OAuth2UserInfo create(String path, OAuth2User user) {
        if(path.equals("/login/oauth2/code/google"))
            return new GoogleOAuth2UserInfo(user.getAttributes());

        if(path.equals("/login/oauth2/code/kakao"))
            return new KakaoOAuth2UserInfo(user.getAttributes());

        throw new IllegalArgumentException("지원하지 않는 소셜 서비스입니다: " + path);
    }

}
