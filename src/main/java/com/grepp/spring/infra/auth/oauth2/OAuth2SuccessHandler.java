package com.grepp.spring.infra.auth.oauth2;

import com.grepp.spring.app.model.auth.AuthService;
import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.auth.oauth2.user.OAuth2UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        OAuth2UserInfo userInfo = OAuth2UserInfo.create(request.getRequestURI(), user);
        TokenDto dto = authService.processTokenSignin(userInfo);

        ResponseCookie accessTokenCookie = TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(),
            dto.getAccessToken(), dto.getExpiresIn());
        ResponseCookie refreshTokenCookie = TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(),
            dto.getRefreshToken(), dto.getExpiresIn());

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        log.info("로그인 완료.");

        // 추후 Front Server 주소에 따라 값을 바꾸거나, 유동적으로 처리할 방법을 반영하겠습니다.
        // 현재는 로컬 테스트를 기준으로 작성하였습니다.
        String frontEndRedirectUrl = "https://localhost:3000/auth/callback";

//        getRedirectStrategy().sendRedirect(request,response,frontEndRedirectUrl);
        // 일단 백엔드 루트 디렉토리로 해뒀는데, 만약 로컬 프론트를 테스트 한다면 위의 값으로 변경
        getRedirectStrategy().sendRedirect(request,response,"/");
    }
}
