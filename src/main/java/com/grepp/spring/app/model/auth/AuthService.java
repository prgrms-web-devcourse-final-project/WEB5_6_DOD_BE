package com.grepp.spring.app.model.auth;

import com.grepp.spring.app.controller.api.auth.Provider;
import com.grepp.spring.app.controller.api.auth.payload.request.LoginRequest;
import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.auth.dto.NewTokensDto;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.mainpage.entity.Calendar;
import com.grepp.spring.app.model.member.code.Role;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.repository.CalendarRepository;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.auth.jwt.dto.AccessTokenDto;
import com.grepp.spring.infra.auth.jwt.dto.RefreshTokenDto;
import com.grepp.spring.infra.auth.oauth2.user.OAuth2UserInfo;
import com.grepp.spring.infra.error.exceptions.member.InvalidTokenException;
import com.grepp.spring.infra.response.ResponseCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final CalendarRepository calendarRepository;
    private final RefreshTokenService refreshTokenService;

    // 나중에 삭제할 임시 로그인 메서드.
    public TokenDto signin(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginRequest.getId(),
                loginRequest.getPassword());

        // loadUserByUsername + password 검증 후 인증 객체 반환
        // 인증 실패 시: AuthenticationException 발생
        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String roles =  String.join(",", authentication.getAuthorities().stream().map(
            GrantedAuthority::getAuthority).toList());
        return processTokenSigninTest(authentication.getName(), roles);
    }

    @Transactional
    public TokenDto processTokenSignin(OAuth2UserInfo userInfo) {

        // id는 provider 와 providerId 를 합쳐서 생성
        String userId = userInfo.getProvider() + "_" + userInfo.getProviderId();

        Optional<Member> existMember = memberRepository.findById(userId);
        Member member;

        // 신규 유저라면 DB에 저장
        if (existMember.isEmpty()) {
            member = new Member();
            member.setId(userId);
            member.setProvider(Provider.valueOf(userInfo.getProvider()));
            member.setName(userInfo.getName());
            member.setEmail(userInfo.getEmail());
            member.setRole(Role.ROLE_USER);
            member.setProfileImageNumber(new Random().nextInt(8));
            member.setPassword("123qwe!@#");
            // 일단 전화번호는 나중에 받고
            // 프로필 사진은 모르겠다 일단 아무 숫자나 넣자
            memberRepository.save(member);
            log.info("새로운 유저 DB에 저장 완료: {}", userId);

            memberRepository.flush(); // DB 에 즉시 insert !!

            // 캘린더 생성
            Calendar calendar = new Calendar();
            calendar.setMember(member);
            calendar.setName("ittaeok");
            calendar.setSynced(false);
            calendar.setSyncUpdatedAt(LocalDateTime.now());
            calendarRepository.save(calendar);

            log.info("새로운 유저 및 캘린더 저장 완료: {}", userId);
        }

        AccessTokenDto accessToken = jwtTokenProvider.generateAccessToken(userId, Role.ROLE_USER.name());

        RefreshTokenDto refreshToken = jwtTokenProvider.generateRefreshToken(accessToken.getJti());


        return TokenDto.builder()
            .accessToken(accessToken.getToken())
            .atId(accessToken.getJti())
            .expiresIn(accessToken.getExpires())
            .refreshToken(refreshToken.getToken())
            .refreshExpiresIn(refreshToken.getExpires())
            .grantType("Bearer")
            .userId(userId)
            .userName(userInfo.getName())
            .build();

    }

    // 나중에 삭제할 임시 로그인 메서드.
    public TokenDto processTokenSigninTest(String userId, String roles) {

        AccessTokenDto accessToken = jwtTokenProvider.generateAccessToken(userId, roles);
        RefreshTokenDto refreshToken = jwtTokenProvider.generateRefreshToken(accessToken.getJti());

        // Redis에 저장.
        refreshTokenService.saveWithAtId(RefreshToken.builder()
            .id(refreshToken.getJti())
            .atId(accessToken.getJti())
            .ttl(refreshToken.getExpires())
            .build());

        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new BadCredentialsException("인증된 사용자를 찾을 수 없습니다."));

        return TokenDto.builder()
            .accessToken(accessToken.getToken())
            .atId(accessToken.getJti())
            .grantType("Bearer")
            .refreshToken(refreshToken.getToken())
            .refreshExpiresIn(jwtTokenProvider.getRefreshTokenExpiration())
            .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
            .userId(userId)
            .userName(member.getName())
            .build();
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {

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
        log.info("토큰이 무효화되었습니다.");
    }

    @Transactional
    public NewTokensDto updateTokens(String accessToken, String refreshToken) {

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

        log.info("갱신된 Access Token: {}", newAccessTokenDto.getJti());
        log.info("갱신된 Refresh Token: {}", newRefreshTokenDto.getJti());

        return new NewTokensDto(newAccessTokenDto, newRefreshTokenDto);
    }

}
