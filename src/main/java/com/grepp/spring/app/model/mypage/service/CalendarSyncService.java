package com.grepp.spring.app.model.mypage.service;

import com.grepp.spring.app.model.mainpage.service.GoogleScheduleService;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.entity.SocialAuthToken;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.member.repository.SocialAuthTokenRepository;
import com.grepp.spring.app.model.mypage.dto.GoogleEventDto;
import com.grepp.spring.app.model.mypage.repository.CalendarRepository;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CalendarSyncService {

  private final MemberRepository memberRepository;
  private final SocialAuthTokenRepository socialAuthTokenRepository;
  private final CalendarRepository calendarRepository;

  private final SocialAuthTokenService socialAuthTokenService; // refresh_token → access_token 자동 갱신 담당
  private final GoogleOAuthService googleOAuthService;
  private final GoogleScheduleService googleScheduleService;

  private final RestTemplate restTemplate = new RestTemplate();

  // 캘린더 동기화 (매번 새로고침해서 일정 받아오는 방식)
  public ApiResponse<List<GoogleEventDto>> syncCalendar(String memberId) {

    // 1) 회원 조회
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

    // 2) 사용자 구글 토큰 조회
    Optional<SocialAuthToken> tokenOpt = socialAuthTokenRepository.findByMember(member);
    if (tokenOpt.isEmpty()) {
      // 아예 연동 안 된 상태 → 재인증 필요
      return ApiResponse.error(ResponseCode.UNAUTHORIZED, googleOAuthService.buildReauthUrl());
    }

    SocialAuthToken token = tokenOpt.get();

    // 3) refresh_token으로 유효한 access_token 확보 (만료 시 자동 갱신)
    String accessToken = socialAuthTokenService.getValidAccessToken(token);
    if (accessToken == null) {
      // refresh_token 무효 → 재인증 필요
      return ApiResponse.error(ResponseCode.UNAUTHORIZED, googleOAuthService.buildReauthUrl());
    }

    // 4) 구글 캘린더 API 직접 호출
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<Map> response = restTemplate.exchange(
        "https://www.googleapis.com/calendar/v3/calendars/primary/events",
        HttpMethod.GET,
        entity,
        Map.class
    );

    // 5) 구글 응답 → DTO 변환
    List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");
    List<GoogleEventDto> events = convertToDto(items);

    // 6) DB 저장 (새로운 일정만 저장하거나 업데이트)
    googleScheduleService.syncGoogleEvents(member, events);


    // 최신 이벤트 반환
    return ApiResponse.success(events);
  }

  // Google JSON → DTO 변환
  private List<GoogleEventDto> convertToDto(List<Map<String, Object>> items) {
    List<GoogleEventDto> events = new ArrayList<>();

    if (items != null) {
      for (Map<String, Object> item : items) {
        String id = (String) item.get("id");
        String summary = (String) item.get("summary");
        String etag = (String) item.get("etag");

        Map<String, String> startMap = (Map<String, String>) item.get("start");
        Map<String, String> endMap = (Map<String, String>) item.get("end");

        // dateTime 우선, 없으면 date 사용
        String startDateTime = startMap.get("dateTime");
        String endDateTime = endMap.get("dateTime");

        LocalDateTime start;
        LocalDateTime end;
        boolean allDay = false; // 초기값 false

        if (startDateTime != null) {
          start = LocalDateTime.parse(startDateTime, DateTimeFormatter.ISO_DATE_TIME);
        } else {
          // 종일 이벤트 → date(YYYY-MM-DD)를 LocalDateTime으로 변환
          start = LocalDate.parse(startMap.get("date"), DateTimeFormatter.ISO_DATE).atStartOfDay();
          allDay = true; // 종일 일정
        }

        if (endDateTime != null) {
          end = LocalDateTime.parse(endDateTime, DateTimeFormatter.ISO_DATE_TIME);
        } else {
          end = LocalDate.parse(endMap.get("date"), DateTimeFormatter.ISO_DATE).atStartOfDay();
        }

        events.add(
            GoogleEventDto.builder()
                .googleEventId(id)
                .title(summary)
                .start(start)
                .end(end)
                .allDay(allDay)
                .etag(etag)
                .build()
        );
      }
    }
    return events;
  }


//  // 연동 상태 조회 (원하면 유지) -> 마지막 새로고침 시간 보여줄 거임?
//  public CalendarSyncStatusResponse getCalendarSyncStatus(String memberId) {
//    Member member = memberRepository.findById(memberId)
//        .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
//
//    Calendar calendar = calendarRepository.findByMember(member)
//        .orElseThrow(() -> new IllegalStateException("캘린더가 존재하지 않습니다."));
//
//    return new CalendarSyncStatusResponse(
//        calendar.getId(),
//        calendar.getName(),
//        calendar.getSynced()
//    );
//  }

  }


