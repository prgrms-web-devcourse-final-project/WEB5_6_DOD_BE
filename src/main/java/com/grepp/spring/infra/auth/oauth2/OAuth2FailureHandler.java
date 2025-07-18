package com.grepp.spring.infra.auth.oauth2;

import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final JwtTokenProvider jwtProvider;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException {

        String requestAccessToken = jwtProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
        if (requestAccessToken == null) {
            return;
        }

        Claims claims = jwtProvider.getClaims(requestAccessToken);

        ResponseCookie expiredAccessToken = TokenCookieFactory.createExpiredToken(AuthToken.ACCESS_TOKEN.toString());
        ResponseCookie expiredRefreshToken = TokenCookieFactory.createExpiredToken(AuthToken.REFRESH_TOKEN.toString());
        response.addHeader("Set-Cookie", expiredAccessToken.toString());
        response.addHeader("Set-Cookie", expiredRefreshToken.toString());
        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}
