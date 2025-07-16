package com.grepp.spring.app.model.auth;

import com.grepp.spring.app.controller.api.auth.Provider;
import com.grepp.spring.app.controller.api.auth.payload.request.LoginRequest;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.mainpage.entity.Calendar;
import com.grepp.spring.app.model.member.code.Role;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.repository.CalendarRepository;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.dto.AccessTokenDto;
import com.grepp.spring.infra.auth.oauth2.user.OAuth2UserInfo;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
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

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final CalendarRepository calendarRepository;

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
            member.setProfileImageNumber((long) new Random().nextInt(10));
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
        RefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(accessToken.getJti());

        return TokenDto.builder()
            .accessToken(accessToken.getToken())
            .expiresIn(accessToken.getExpires()) // 만료기간 엑세스 토큰만 해도 되는지 ?
            .refreshToken(refreshToken.getToken())
            .userId(userId)
            .userName(userInfo.getName())
            .build();

    }

    public TokenDto processTokenSigninTest(String userId, String roles) {

        AccessTokenDto accessToken = jwtTokenProvider.generateAccessToken(userId, roles);
        RefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(accessToken.getJti());

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
