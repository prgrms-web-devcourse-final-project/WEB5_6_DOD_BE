package com.grepp.spring.infra.auth.oauth2.user;

import java.util.Map;

public class GreppOAuth2UserInfo implements OAuth2UserInfo{
    
    private final Map<String, Object> attributes;
    
    public GreppOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    
    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }
    
    @Override
    public String getProvider() {
        return "grepp";
    }
    
    @Override
    public String getName() {
        return attributes.get("sub").toString();
    }
    
    @Override
    public String getPicture() {
        return "";
    }
}
