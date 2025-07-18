package com.grepp.spring.app.model.auth.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveWithAtId(RefreshToken refreshToken){
        redisTemplate.opsForValue().set(
            refreshToken.getAtId(), // Redis 의 key 로 사용할 엑세스 토큰 Jti
            refreshToken,
            Duration.ofSeconds(refreshToken.getTtl()));
    }

    public void deleteByAccessTokenId(String atId) {
        redisTemplate.delete(atId);
    }

    public RefreshToken findByAccessTokenId(String atId) {
        return objectMapper.convertValue(redisTemplate.opsForValue().get(atId), RefreshToken.class);
    }

}
