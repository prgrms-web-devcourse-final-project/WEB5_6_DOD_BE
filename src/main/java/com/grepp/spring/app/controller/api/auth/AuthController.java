package com.grepp.spring.app.controller.api.auth;

import com.grepp.spring.app.controller.api.auth.payload.request.LoginRequest;
import com.grepp.spring.app.controller.api.auth.payload.response.TokenResponse;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.auth.AuthService;
import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.member.code.Role;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.auth.jwt.dto.AccessTokenDto;
import com.grepp.spring.infra.auth.jwt.dto.RefreshTokenDto;
import com.grepp.spring.infra.error.exceptions.AuthApiException;
import com.grepp.spring.infra.error.exceptions.member.InvalidTokenException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    // 백엔드의 편안한 테스트를 위해 로그인을 살려보겠습니다.
    // 나중에 삭제할 임시 로그인 메서드.
    @Operation(summary = "로그인", description = "토큰을 발급합니다. 백엔드에서 소셜 로그인이 힘들 때 사용하세요!!")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
        @Valid @RequestBody LoginRequest loginRequest,
        HttpServletResponse response
    ) {

        try {
            TokenDto tokenDto = authService.signin(loginRequest);

            ResponseCookie accessTokenCookie = TokenCookieFactory.create(
                AuthToken.ACCESS_TOKEN.name(), tokenDto.getAccessToken(), tokenDto.getExpiresIn());
            response.addHeader("Set-Cookie", accessTokenCookie.toString());

            ResponseCookie refreshTokenCookie = TokenCookieFactory.create(
                AuthToken.REFRESH_TOKEN.name(), tokenDto.getRefreshToken(), tokenDto.getRefreshExpiresIn());
            response.addHeader("Set-Cookie", refreshTokenCookie.toString());

            return ResponseEntity.ok(ApiResponse.success(TokenResponse.builder()
                .userId(tokenDto.getUserId())
                .userName(tokenDto.getUserName())
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .expiresIn(tokenDto.getExpiresIn())
                .refreshToken(tokenDto.getRefreshToken())
                .refreshExpiresIn(tokenDto.getRefreshExpiresIn())
                .build()));

        } catch (BadCredentialsException e) {
            throw new InvalidTokenException(ResponseCode.INVALID_TOKEN, "엑세스 토큰과 리프레시 토큰의 정보가 일치하지 않습니다.");
        }
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 토큰 제거
        ResponseCookie deleteAccessTokenCookie = TokenCookieFactory.createExpiredToken(AuthToken.ACCESS_TOKEN.name());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessTokenCookie.toString());

        ResponseCookie deleteRefreshTokenCookie = TokenCookieFactory.createExpiredToken(AuthToken.REFRESH_TOKEN.name());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshTokenCookie.toString());

        ResponseCookie deleteSessionIdCookie = TokenCookieFactory.createExpiredToken(AuthToken.AUTH_SERVER_SESSION_ID.name());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteSessionIdCookie.toString());

        // Redis에서 리프레시 토큰 제거
        String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            String atJti = jwtTokenProvider.getClaims(accessToken).getId();
            refreshTokenService.deleteByAccessTokenId(atJti);
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 보내 엑세스 토큰과 리프레시 토큰을 갱신합니다.")
    @PostMapping("/update-tokens")
    public ResponseEntity<ApiResponse<Void>> updateAccessToken(HttpServletRequest request, HttpServletResponse response) {

        // 요청에서 현재 가지고 있는 토큰을 추출하기
        String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
        String refreshToken = jwtTokenProvider.resolveToken(request, AuthToken.REFRESH_TOKEN);

        // 리프레시 토큰의 유효성 검증
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)){
            throw new InvalidTokenException(ResponseCode.INVALID_TOKEN, "엑세스 토큰과 리프레시 토큰의 정보가 일치하지 않습니다.");
        }

        // 토큰에서 Claim 을 추출합시다.
        Claims accessClaims = jwtTokenProvider.getClaims(accessToken);
        String atJtiFromAccess = accessClaims.getId(); // 기존 Access Token의 JTI
        String userId = accessClaims.getSubject();

        Claims refreshClaims = jwtTokenProvider.getClaims(refreshToken);
        String atJtiFromRefresh = refreshClaims.get("atId", String.class);
        String refreshJti = refreshClaims.getId(); // Refresh Token의 JTI

        // 엑세스 토큰과 리프레시 토큰의 JTI 일치 여부 확인
        if (!atJtiFromAccess.equals(atJtiFromRefresh)) {
            // 일치하지 않으면 즉시 Redis에서 제거
            refreshTokenService.deleteByAccessTokenId(atJtiFromRefresh);
            throw new InvalidTokenException(ResponseCode.INVALID_TOKEN, "엑세스 토큰과 리프레시 토큰의 정보가 일치하지 않습니다.");
        }

        // Redis에 저장된 리프레시 토큰 조회 및 검증
        RefreshToken currentRefreshToken = refreshTokenService.findByAccessTokenId(atJtiFromRefresh);
        if (currentRefreshToken == null || !currentRefreshToken.getId().equals(refreshJti)) {
            refreshTokenService.deleteByAccessTokenId(atJtiFromRefresh);
            throw new InvalidTokenException(ResponseCode.INVALID_TOKEN, "엑세스 토큰과 리프레시 토큰의 정보가 일치하지 않습니다.");
        }

        // 새로운 Access Token 생성
        AccessTokenDto newAccessTokenDto = jwtTokenProvider.generateAccessToken(userId, Role.ROLE_USER.name());
        // 새로운 Refresh token 생성
        RefreshTokenDto newRefreshTokenDto = jwtTokenProvider.generateRefreshToken(
            newAccessTokenDto.getJti());
        // 기존에 Redis에 저장된 Refresh Token 삭제 및 새로운 토큰 저장
        refreshTokenService.deleteByAccessTokenId(atJtiFromRefresh);

        // 새 Refresh Token을 Redis 에 저장합니다.
        RefreshToken newRefreshToken = RefreshToken.builder()
            .id(newRefreshTokenDto.getJti())
            .atId(newAccessTokenDto.getJti())
            .ttl(jwtTokenProvider.getRefreshTokenExpiration())
            .build();
        refreshTokenService.saveWithAtId(newRefreshToken);

        // 쿠키에 토큰 저장
        ResponseCookie newAccessTokenCookie = TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(),
            newAccessTokenDto.getToken(), newAccessTokenDto.getExpires());
        ResponseCookie newRefreshTokenCookie = TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(),
            newRefreshTokenDto.getToken(), newRefreshTokenDto.getExpires());

        response.addHeader("Set-Cookie", newAccessTokenCookie.toString());
        response.addHeader("Set-Cookie", newRefreshTokenCookie.toString());

        log.info("갱신된 Access Token: {}", newAccessTokenDto.getToken());
        log.info("갱신된 Refresh Token: {}", newRefreshTokenDto.getToken());

        return ResponseEntity.ok(ApiResponse.success("토큰이 갱신되었습니다."));
    }
}
