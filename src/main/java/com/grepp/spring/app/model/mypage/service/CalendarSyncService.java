package com.grepp.spring.app.model.mypage.service;

import com.grepp.spring.app.model.mainpage.service.GoogleScheduleService;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.entity.SocialAuthToken;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.member.repository.SocialAuthTokenRepository;
import com.grepp.spring.app.model.mypage.dto.GoogleEventDto;
import com.grepp.spring.infra.error.exceptions.mypage.CalendarAuthRequiredException;
import com.grepp.spring.infra.error.exceptions.mypage.CalendarEventSaveFailedException;
import com.grepp.spring.infra.error.exceptions.mypage.CalendarSyncFailedException;
import com.grepp.spring.infra.error.exceptions.mypage.InvalidCalendarResponseException;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CalendarSyncService {

  private final MemberRepository memberRepository;
  private final SocialAuthTokenRepository socialAuthTokenRepository;

  private final SocialAuthTokenService socialAuthTokenService; // refresh_token → access_token 자동 갱신 담당
  private final GoogleOAuthService googleOAuthService;
  private final GoogleScheduleService googleScheduleService;

  private final RestTemplate restTemplate = new RestTemplate();

  // 캘린더 동기화 (매번 새로고침해서 일정 받아오는 방식)
  public List<GoogleEventDto> syncCalendar(String memberId) {

    // 1) 회원 조회
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));

    // 2) 사용자 구글 토큰 조회
    SocialAuthToken token = socialAuthTokenRepository.findByMember(member)
        .orElseThrow(() -> new CalendarAuthRequiredException(
            MyPageErrorCode.CALENDAR_AUTH_REQUIRED,
            googleOAuthService.buildReauthUrl()
        ));

    // 3) refresh_token으로 유효한 access_token 확보 (만료 시 자동 갱신)
    String accessToken = socialAuthTokenService.getValidAccessToken(token);

    try {
      // 4) 구글 캘린더 API 호출
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

      // 6) DB 저장
      googleScheduleService.syncGoogleEvents(member, events);

      // 최신 이벤트 반환
      return events;

    } // Google Calendar API 호출 시에 생기는 예외 처리
    catch (HttpClientErrorException e) {
      // 구글 API 호출 실패
      throw new CalendarSyncFailedException(MyPageErrorCode.CALENDAR_SYNC_FAILED);
    } catch (DataAccessException e) {
      // DB 저장 실패
      throw new CalendarEventSaveFailedException(MyPageErrorCode.CALENDAR_SYNC_FAILED);
    } catch (Exception e) {
      // JSON 파싱 실패
      throw new InvalidCalendarResponseException(MyPageErrorCode.INVALID_CALENDAR_RESPONSE);
    }
  }

  // Google JSON 응답 → 내부 DTO 변환
  // 구글 api 에서 내려주는 event(일정) 의 start/end 필드 파싱
  private List<GoogleEventDto> convertToDto(List<Map<String, Object>> items) {
    List<GoogleEventDto> events = new ArrayList<>();

    if (items != null) {
      for (Map<String, Object> item : items) {
        String id = (String) item.get("id");
        String summary = (String) item.get("summary");
        String etag = (String) item.get("etag");

        Map<String, String> startMap = (Map<String, String>) item.get("start");
        Map<String, String> endMap = (Map<String, String>) item.get("end");

        // dateTime 필드 있으면 시간 기반 일정, 없으면 종일 일정(시간 없이 날짜만 date(start,date)만)
        String startDateTime = startMap.get("dateTime");
        String endDateTime = endMap.get("dateTime");

        LocalDateTime start;
        LocalDateTime end;
        boolean allDay = false; // 초기값 false

        if (startDateTime != null) {
          // 시간 기반 일정
          start = LocalDateTime.parse(startDateTime, DateTimeFormatter.ISO_DATE_TIME);
        } else {
          // 종일 일정 → date(YYYY-MM-DD)를 LocalDateTime으로 변환
          start = LocalDate.parse(startMap.get("date"), DateTimeFormatter.ISO_DATE)
              .atStartOfDay(); // 00:00:00 붙여서 LocalDateTime 으로 변환. 내부에서는 시간 정보 있어야 저장 가능
          allDay = true; // 종일 일정, 시간 표시 안함
        }

        if (endDateTime != null) {
          end = LocalDateTime.parse(endDateTime, DateTimeFormatter.ISO_DATE_TIME);
        } else {
          end = LocalDate.parse(endMap.get("date"), DateTimeFormatter.ISO_DATE).atStartOfDay();
        }

        // dto 생성
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

}


