package com.grepp.spring.app.model.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.spring.app.controller.api.event.payload.response.AllTimeScheduleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventScheduleCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY_PREFIX = "event:all-time:";

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);

    // 캐시에서 전체 시간대 조회 결과를 가져옴. 캐시 조회 실패 시 null 반환
    public AllTimeScheduleResponse getCachedAllTimeSchedule(Long eventId) {
        try {
            String key = buildCacheKey(eventId);
            Object cached = redisTemplate.opsForValue().get(key);

            if (cached == null) {
                return null;
            }

            AllTimeScheduleResponse response = objectMapper.convertValue(cached, AllTimeScheduleResponse.class);

            if (response != null) {
                log.debug("Cache hit for eventId: {}", eventId);
                return response;
            }

            return null;

        } catch (Exception e) {
            log.warn("Cache get failed for eventId: {}, falling back to DB: {}", eventId, e.getMessage());
            return null;
        }
    }

    // 전체 시간대 조회 결과를 캐시에 저장
    public void cacheAllTimeSchedule(Long eventId, AllTimeScheduleResponse response) {
        String key = buildCacheKey(eventId);
        redisTemplate.opsForValue().set(key, response, DEFAULT_TTL);

        log.debug("Cached data for eventId: {}", eventId);
    }

    // 특정 이벤트의 캐시를 무효화
    public void invalidateEventCache(Long eventId) {
        String key = buildCacheKey(eventId);
        Boolean deleted = redisTemplate.delete(key);

        log.debug("Cache invalidated for eventId: {}, deleted: {}", eventId, deleted);
    }

    // 캐시 키 생성
    private String buildCacheKey(Long eventId) {
        return CACHE_KEY_PREFIX + eventId;
    }
}