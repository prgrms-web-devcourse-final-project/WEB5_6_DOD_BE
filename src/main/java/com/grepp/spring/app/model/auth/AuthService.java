package com.grepp.spring.app.model.auth;

import com.grepp.spring.app.controller.api.auth.payload.LoginRequest;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.dto.AccessTokenDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;

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
        return processTokenSignin(authentication.getName(), roles);
    }


    public TokenDto processTokenSignin(String userId, String roles) {

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        AccessTokenDto accessToken = jwtTokenProvider.generateAccessToken(userId, roles);
        RefreshToken refreshToken = refreshTokenService.saveWithAtId(accessToken.getJti());

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

}
