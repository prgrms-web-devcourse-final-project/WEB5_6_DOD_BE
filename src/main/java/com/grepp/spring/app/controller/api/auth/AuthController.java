package com.grepp.spring.app.controller.api.auth;

import com.grepp.spring.app.controller.api.auth.payload.request.LoginRequest;
import com.grepp.spring.app.controller.api.auth.payload.response.TokenResponse;
import com.grepp.spring.app.model.auth.dto.NewTokensDto;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.auth.AuthService;
import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.error.exceptions.member.InvalidTokenException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        authService.logout(request, response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 보내 엑세스 토큰과 리프레시 토큰을 갱신합니다.")
    @PostMapping("/update-tokens")
    public ResponseEntity<ApiResponse<Void>> updateAccessToken(HttpServletRequest request, HttpServletResponse response) {

        // 요청에서 현재 가지고 있는 토큰을 추출하기
        String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
        String refreshToken = jwtTokenProvider.resolveToken(request, AuthToken.REFRESH_TOKEN);

        // Service 에게 비즈니스 로직 모두 위임
        NewTokensDto newTokens = authService.updateTokens(accessToken, refreshToken);

        // 쿠키에 토큰 저장
        ResponseCookie newAccessTokenCookie = TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(),
            newTokens.getNewAccessToken().getToken(), newTokens.getNewAccessToken().getExpires());
        ResponseCookie newRefreshTokenCookie = TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(),
            newTokens.getNewRefreshToken().getToken(), newTokens.getNewRefreshToken().getExpires());

        response.addHeader("Set-Cookie", newAccessTokenCookie.toString());
        response.addHeader("Set-Cookie", newRefreshTokenCookie.toString());

        return ResponseEntity.ok(ApiResponse.success("토큰이 갱신되었습니다."));
    }
}
