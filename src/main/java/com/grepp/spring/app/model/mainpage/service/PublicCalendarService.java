package com.grepp.spring.app.model.mainpage.service;

import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.dto.PublicCalendarEventDto;
import com.grepp.spring.app.model.mypage.service.PublicCalendarIdService;
import com.grepp.spring.infra.error.exceptions.mypage.GoogleCalendarApiFailedException;
import com.grepp.spring.infra.error.exceptions.mypage.InvalidPublicCalendarIdException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PublicCalendarService {

  private PublicCalendarIdService publicCalendarIdService;

  private MemberRepository memberRepository;

  @Value("${google.api.key}")
  private String googleApiKey;

  // 서버 - 서버 통신, API 호출 위해 사용 json 변환까지 지원
  private final RestTemplate restTemplate = new RestTemplate();

  public List<PublicCalendarEventDto> fetchPublicCalendarEvents(String publicCalendarId) {

    // google api url 만들어주기 (이벤트까지 뜯어와야 하니까, 이벤트 아이디는 item 안에 딸려옴)
    String url = "https://www.googleapis.com/calendar/v3/calendars/"
        + publicCalendarId + "/events?key=" + googleApiKey;

    try {
      // google api 호출하기
      ResponseEntity<Map> response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          null, // 요청 본문 없음
          Map.class
      );

      // 응답 JSON 파싱
      Map<String, Object> body = response.getBody();
      if (body == null || !body.containsKey("items")) { // null 일 때 빈 리스트 반환
        return Collections.emptyList();
      }

      // items 배열 꺼내기
      List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

      List<PublicCalendarEventDto> result = new ArrayList<>();

      // 이벤트 데이터 처리 (구글 일정)
      for (Map<String, Object> item : items) {

        String eventId = (String) item.get("id");
        String title = (String) item.get("summary");
        String status = (String) item.get("status");
        //    String htmlLink = (String) item.get("htmlLink");
        // status: confirmed, tentative, cancelled

        // 시작/종료는 dateTime 또는 date 중 하나가 옴
        // date: 종일 이벤트 (0723-0724), dateTime: 일반 + 걸쳐있는 이벤트에 timezone 까지...
        Map<String, String> startMap = (Map<String, String>) item.get("start");
        Map<String, String> endMap = (Map<String, String>) item.get("end");

        // getOrDefault: dateTime 있으면 그걸 쓰고 (일반 일정), 없으면 디폴트인 date (종일 일정)
        String start = startMap.getOrDefault("dateTime", startMap.get("date"));
        String end = endMap.getOrDefault("dateTime", endMap.get("date"));
        // startMap 에 date 키가 있으면 종일 일정 -> true
        boolean isAllDay = startMap.containsKey("date");

        PublicCalendarEventDto dto = PublicCalendarEventDto.builder()
            .eventId(eventId)
            .title(title)
            .start(start)
            .end(end)
            .allDay(isAllDay)
            .status(status)
            //        .htmlLink(htmlLink)
            .build();

        result.add(dto);
      }
      return result;

    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND || e.getStatusCode() == HttpStatus.FORBIDDEN) {
        // 잘못된/권한 없는 캘린더 -> 사용자 잘못..
        throw new InvalidPublicCalendarIdException(MyPageErrorCode.INVALID_PUBLIC_CALENDAR_ID);
      }
      // 그 외 400 번대 -> 외부 API 문제로 간주
      throw new GoogleCalendarApiFailedException(MyPageErrorCode.GOOGLE_CALENDAR_API_FAILED);
    } catch (RestClientException e) {
      // 그외 네트워크, 500 번대 등 -> 외부 API 문제
      throw new GoogleCalendarApiFailedException(MyPageErrorCode.GOOGLE_CALENDAR_API_FAILED);
    }
  }
}
