package com.grepp.spring.infra.auth.jwt.filter;

import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.auth.jwt.dto.AccessTokenDto;
import com.grepp.spring.infra.error.exceptions.CommonException;
import com.grepp.spring.infra.response.ResponseCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

//    private final UserBlackListRepository userBlackListRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        List<String> excludePath = new ArrayList<>();
        excludePath.addAll(List.of("/api/v1/auth/update-tokens", "/api/v1/auth/login",  "/favicon.ico", "/img", "/js","/css","/download"));
        excludePath.addAll(List.of("/error", "/v3/api-docs", "/swagger-ui", "/swagger-ui.html"));
        String path = request.getRequestURI();
        return excludePath.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtTokenProvider.validateToken(accessToken)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            manageTokenRefresh(accessToken, request, response);
        }
        filterChain.doFilter(request, response);
    }

    private void manageTokenRefresh(
        String accessToken,
        HttpServletRequest request,
        HttpServletResponse response) throws IOException {

        // 엑세스 토큰에서 정보(claim) 추출
        Claims claims  = jwtTokenProvider.getClaims(accessToken);
        String userId = claims.getSubject();
        String accessTokenId = claims.getId();

        String refreshToken = jwtTokenProvider.resolveToken(request, AuthToken.REFRESH_TOKEN);

        if (refreshToken == null){
            throw new CommonException(ResponseCode.INVALID_TOKEN);
        }

        // 리프레시 토큰에서 claim 추출
        Claims refreshTokenClaims = jwtTokenProvider.getClaims(refreshToken);
        // 그로부터 Access Token ID 추출
        String atId = (String) refreshTokenClaims.get("atId");

        if (!accessTokenId.equals(atId)) {
            // 추후 블랙리스트 처리 고려 -> Redis
            throw new CommonException(ResponseCode.SECURITY_INCIDENT);
        }

        addToken(response, userId, (String) claims.get("roles"));
    }

    private void addToken(HttpServletResponse response, String userId, String roles) {

        // 새로운 엑세스 토큰 생성
        AccessTokenDto newAccessToken = jwtTokenProvider.generateAccessToken(userId, roles);

        // 새로운 엑세스 토큰의 JTI를 사용하여 새로운 리프레시 토큰 생성
        RefreshToken newRefreshToken = jwtTokenProvider.generateRefreshToken(newAccessToken.getJti());

        // SecurityContextHolder에 인증 정보를 업데이트 해야함.
        // 새로운 엑세스 토큰으로 인증 객체를 생성하고 Context에 지정해줍시다.
        Authentication authentication = jwtTokenProvider.getAuthentication(newAccessToken.getToken());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // client 요청에 자동으로 포함되도록 쿠키에 넣어주기
        ResponseCookie accessTokenCookie = TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(),
            newAccessToken.getToken(), newAccessToken.getExpires());
        ResponseCookie refreshTokenCookie = TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(),
            newRefreshToken.getToken(), newRefreshToken.getTtl());

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
}