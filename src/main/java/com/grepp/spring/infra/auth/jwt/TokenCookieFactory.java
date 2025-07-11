package com.grepp.spring.infra.auth.jwt;

import org.springframework.http.ResponseCookie;

public class TokenCookieFactory {
    public static ResponseCookie create(String name, String value, Long expires) {
        return ResponseCookie.from(name, value)
                   .maxAge(expires)
                   .path("/")
                   .httpOnly(true)             // HttpOnly
                   .secure(true) // 로컬 간에 테스트 하고 싶으면 true, 백엔드를 배포하면 false
                   .sameSite("None") // CORS Problem 예방을 위해 Lax > None 변경
                   .build();
    }
    
    public static ResponseCookie createExpiredToken(String name) {
        return ResponseCookie.from(name, "")
                   .maxAge(0)
                   .path("/")
                   .httpOnly(true)             // HttpOnly
                   .secure(true) // 로컬 간에 테스트 하고 싶으면 true, 백엔드를 배포하면 false
                   .sameSite("None") // CORS Problem 예방을 위해 Lax > None 변경
                   .build();
    }
}
