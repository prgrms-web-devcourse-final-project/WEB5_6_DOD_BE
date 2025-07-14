package com.grepp.spring.app.controller.api.auth;

import com.grepp.spring.app.controller.api.auth.payload.request.AccountDeactivateRequest;
import com.grepp.spring.app.controller.api.auth.payload.response.AccountDeactivateResponse;
import com.grepp.spring.app.controller.api.auth.payload.response.GroupAdminResponse;
import com.grepp.spring.app.controller.api.auth.payload.request.LoginRequest;
import com.grepp.spring.app.controller.api.auth.payload.request.SocialAccountConnectionRequest;
import com.grepp.spring.app.controller.api.auth.payload.response.SocialAccountConnectionResponse;
import com.grepp.spring.app.controller.api.auth.payload.response.SocialAccountResponse;
import com.grepp.spring.app.controller.api.auth.payload.response.TokenResponse;
import com.grepp.spring.app.controller.api.auth.payload.response.UpdateAccessTokenResponse;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.auth.AuthService;
import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.member.code.Role;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.auth.jwt.dto.AccessTokenDto;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.BadCredentialsException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    // 백엔드의 편안한 테스트를 위해 로그인을 살려보겠습니다.
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
            return ResponseEntity.status(401)
                .body(ApiResponse.error(ResponseCode.INVALID_TOKEN, e.getMessage()));
        }
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletResponse response) {

        ResponseCookie deleteAccessTokenCookie = TokenCookieFactory.createExpiredToken(AuthToken.ACCESS_TOKEN.name());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessTokenCookie.toString());

        ResponseCookie deleteRefreshTokenCookie = TokenCookieFactory.createExpiredToken(AuthToken.REFRESH_TOKEN.name());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshTokenCookie.toString());

        ResponseCookie deleteSessionIdCookie = TokenCookieFactory.createExpiredToken(AuthToken.AUTH_SERVER_SESSION_ID.name());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteSessionIdCookie.toString());

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @Operation(summary = "소셜 연동 조회", description = "계정에 연동된 소셜 계정들을 조회합니다.")
    @GetMapping("/social-connections")
    public ResponseEntity<ApiResponse<?>> socialAccount() {
        return ResponseEntity.ok(ApiResponse.success(
            Map.of("socialAccounts", List.of(new SocialAccountResponse("jgnsjn198283718", Provider.GOOGLE),
                new SocialAccountResponse("892bgdh71hb2dda", Provider.KAKAO)))));
    }

    @Operation(summary = "소셜 연동 요청", description = "계정에 새로운 소셜 계정을 연동합니다."
        + "test AuthorizationCode : dkftndjqtsmsdlswmdzhem123")
    @PostMapping("/social-connections")
    public ResponseEntity<ApiResponse<?>> socialAccountConnections(@Valid @RequestBody SocialAccountConnectionRequest request) {
        if (request.getAuthorizationCode().equals("dkftndjqtsmsdlswmdzhem123")) {
            return ResponseEntity.ok(ApiResponse.success(
                new SocialAccountConnectionResponse(Provider.GOOGLE)
            ));
        } else {
            return ResponseEntity.status(400)
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
        }
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 보내 엑세스 토큰과 리프레시 토큰을 갱신합니다.")
    @PostMapping("/update-tokens")
    public ResponseEntity<ApiResponse<?>> updateAccessToken(HttpServletRequest request, HttpServletResponse response) {

        // 요청에서 현재 가지고 있는 토큰을 추출하기
        String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
        String refreshToken = jwtTokenProvider.resolveToken(request, AuthToken.REFRESH_TOKEN);

        // 리프레시 토큰의 유효성 검증
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)){
            return ResponseEntity.status(401)
                .body(ApiResponse.error(ResponseCode.INVALID_TOKEN));
        }

        // 추출한 엑세스 토큰의 정보 추출
        Claims claims = jwtTokenProvider.getClaims(accessToken);
        String atJtiFromAccess = claims.getId();
        String userId = claims.getSubject();
        String roles = (String) claims.get("roles");

        // 리프레시 토큰에서 Claim 추출
        Claims refreshClaims = jwtTokenProvider.getClaims(refreshToken);
        String atJtiFromRefresh = (String) refreshClaims.get("atId");

        // 엑세스 토큰과 리프레시 토큰의 JTI 일치 여부 확인
        if (!atJtiFromAccess.equals(atJtiFromRefresh)) {
            return ResponseEntity.status(401)
                .body(ApiResponse.error(ResponseCode.INVALID_TOKEN, "엑세스 토큰과 리프레시 토큰의 정보가 일치하지 않습니다."));
        }

        // 이제 검증 끝났으니 토큰 생성
        AccessTokenDto newAccessToken = jwtTokenProvider.generateAccessToken(userId,
            Role.ROLE_USER.name());
        RefreshToken newRefreshToken = jwtTokenProvider.generateRefreshToken(
            newAccessToken.getJti());

        // 생성한 토큰을 쿠키에 추가
        ResponseCookie newAccessTokenCookie = TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(),
            newAccessToken.getToken(), newAccessToken.getExpires());
        ResponseCookie newRefreshTokenCookie = TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(),
            newRefreshToken.getToken(), newRefreshToken.getTtl());

        response.addHeader("Set-Cookie", newAccessTokenCookie.toString());
        response.addHeader("Set-Cookie", newRefreshTokenCookie.toString());
        log.info("갱신된 엑세스 토큰: "+ newAccessToken.getToken());
        log.info("갱신된 리프레시 토큰: " + newRefreshToken.getToken());

        return ResponseEntity.ok(ApiResponse.success("토큰이 갱신되었습니다."));
    }
}
