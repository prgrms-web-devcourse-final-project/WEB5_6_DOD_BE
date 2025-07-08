package com.grepp.spring.infra.auth.jwt.filter;

import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class LogoutFilter extends OncePerRequestFilter {
    
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        
        String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
        
        if(accessToken == null){
            filterChain.doFilter(request,response);
            return;
        }
        
        String path = request.getRequestURI();
        Claims claims  = jwtTokenProvider.getClaims(accessToken);
        
        if(path.equals("/auth/logout")){
            refreshTokenService.deleteByAccessTokenId(claims.getId());
            SecurityContextHolder.clearContext();
            ResponseCookie expiredAccessToken = TokenCookieFactory.createExpiredToken(AuthToken.ACCESS_TOKEN.name());
            ResponseCookie expiredRefreshToken = TokenCookieFactory.createExpiredToken(AuthToken.REFRESH_TOKEN.name());
            ResponseCookie expiredSessionId = TokenCookieFactory.createExpiredToken(AuthToken.AUTH_SERVER_SESSION_ID.name());
            response.addHeader("Set-Cookie", expiredAccessToken.toString());
            response.addHeader("Set-Cookie", expiredRefreshToken.toString());
            response.addHeader("Set-Cookie", expiredSessionId.toString());
            response.sendRedirect("/");
        }
        
        filterChain.doFilter(request,response);
    }
}
