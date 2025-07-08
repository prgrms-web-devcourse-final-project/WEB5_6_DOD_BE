package com.grepp.spring.infra.auth.oauth2.user;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo{
    
    private final Map<String, Object> attributes;
    
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    
    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }
    
    @Override
    public String getProvider() {
        return "google";
    }
    
    @Override
    public String getName() {
        return attributes.get("login").toString();
    }
    
    @Override
    public String getPicture() {
        return attributes.get("avatar_url") == null ? "" : attributes.get("avatar_url").toString();
    }
}
