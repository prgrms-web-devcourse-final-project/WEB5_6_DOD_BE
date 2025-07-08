//package com.grepp.spring.infra.auth.oauth2;
//
//import com.grepp.spring.app.model.auth.AuthService;
//import com.grepp.spring.app.model.auth.code.AuthToken;
//import com.grepp.spring.app.model.auth.code.Role;
//import com.grepp.spring.app.model.auth.dto.TokenDto;
//import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
//import com.grepp.spring.infra.auth.oauth2.user.OAuth2UserInfo;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseCookie;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final AuthService authService;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//        Authentication authentication) throws IOException, ServletException {
//
//        OAuth2User user = (OAuth2User) authentication.getPrincipal();
//        OAuth2UserInfo userInfo = OAuth2UserInfo.create(request.getRequestURI(), user);
//        TokenDto dto = authService.processTokenSignin(userInfo.getName(), Role.ROLE_USER.toString());
//
//        ResponseCookie accessTokenCookie = TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(),
//            dto.getAccessToken(), dto.getExpiresIn());
//        ResponseCookie refreshTokenCookie = TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(),
//            dto.getRefreshToken(), dto.getExpiresIn());
//
//        response.addHeader("Set-Cookie", accessTokenCookie.toString());
//        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
//        getRedirectStrategy().sendRedirect(request,response,"/");
//    }
//}
