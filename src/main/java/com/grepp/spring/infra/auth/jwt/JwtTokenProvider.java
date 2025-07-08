package com.grepp.spring.infra.auth.jwt;

import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.infra.auth.jwt.dto.AccessTokenDto;
import com.grepp.spring.infra.config.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenProvider {
    
    private final UserDetailsServiceImpl userDetailsService;
    
    @Value("${jwt.secret}")
    private String key;
    
    @Getter
    @Value("${jwt.expiration}")
    private long accessTokenExpiration;
    
    @Getter
    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;
    
    private SecretKey secretKey;
    
    public SecretKey getSecretKey() {
        if (secretKey == null) {
            String keyBase64Encoded = Base64.getEncoder().encodeToString(key.getBytes());
            this.secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes(StandardCharsets.UTF_8));
        }
        
        return this.secretKey;
    }
    
    // Access Token 생성
    public AccessTokenDto generateAccessToken(String username, String roles) {
        String id = UUID.randomUUID().toString();
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + accessTokenExpiration);
        String token = Jwts.builder()
                           .subject(username)
                           .id(id)
                           .claim("roles", roles)
                           .expiration(accessTokenExpiresIn)
                           .signWith(getSecretKey())
                           .compact();
        
        return AccessTokenDto.builder()
                   .jti(id)
                   .token(token)
                   .expires(accessTokenExpiresIn.getTime())
                   .build();
    }
    
    // JWT 토큰을 복호화하여 인증 정보 조회
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        
        Collection<? extends GrantedAuthority> authorities =
            userDetailsService.findUserAuthorities(claims.getSubject());
        
        Principal principal = new Principal(claims.getSubject(), "", authorities);
        principal.setAccessToken(accessToken);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
    
    public Claims getClaims(String accessToken) {
        return parseClaims(accessToken);
    }
    
    // 토큰 유효성 검증
    public boolean validateToken(String token, HttpServletRequest request) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parse(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.", e);
        }
        return false;
    }
    
    public String resolveToken(HttpServletRequest request, AuthToken token) {
        
        String headerToken = request.getHeader("Authorization");
        if (headerToken != null && headerToken.startsWith("Bearer")) {
            return headerToken.substring(7);
        }
        
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        
        return Arrays.stream(cookies)
                   .filter(e -> e.getName().equals(token.name()))
                   .map(Cookie::getValue).findFirst()
                   .orElse(null);
    }
    
    // 토큰에서 클레임(정보) 추출
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(accessToken)
                       .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}