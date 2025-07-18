package com.grepp.spring.infra.auth.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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

        // 엑세스 토큰이 없는 경우 → 인증 없이도 접근 가능한 요청은 패스
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtTokenProvider.validateToken(accessToken)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            // 엑세스 토큰이 만료된 경우
            sendErrorResponse(response, ResponseCode.EXPIRED_TOKEN, ResponseCode.EXPIRED_TOKEN.message());
        } catch (JwtException | IllegalArgumentException e) {
            // 유효하지 않은 토큰인 경우
            sendErrorResponse(response, ResponseCode.INVALID_TOKEN, ResponseCode.INVALID_TOKEN.message());
        }
    }

    private void sendErrorResponse(HttpServletResponse response, ResponseCode code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<?> errorResponse = ApiResponse.error(code, message);
        String json = new ObjectMapper().writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}