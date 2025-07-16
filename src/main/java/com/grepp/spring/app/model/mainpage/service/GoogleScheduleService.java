package com.grepp.spring.app.model.mainpage.service;

import com.grepp.spring.app.model.mainpage.entity.CalendarDetail;
import com.grepp.spring.app.model.mainpage.repository.GoogleScheduleRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.mypage.dto.GoogleEventDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoogleScheduleService { // 구글 캘린더에서 동기화된 일정 조회 담당

  private final GoogleScheduleRepository googleScheduleRepository;

  public List<CalendarDetail> findSchedulesInRange(
      String memberId, LocalDate start, LocalDate end
  ) {
    LocalDateTime startDateTime = start.atStartOfDay();
    LocalDateTime endDateTime = end.atTime(23, 59, 59);

    return googleScheduleRepository.findGoogleSchedulesForMainPage(
        memberId, startDateTime, endDateTime);

  }

  //  syncGoogleEvents() 로 DB 저장
  @Transactional
  public void syncGoogleEvents(Member member, List<GoogleEventDto> googleEvents) {

    // 1) DB에 이미 저장된 googleEventId 목록 조회
    List<String> existingEventIds = googleScheduleRepository
        .findGoogleEventIdsByCalendar(member.getCalendar().getId());

    // 2) 새로운 이벤트만 필터링
    List<CalendarDetail> newDetails = googleEvents.stream()
        .filter(event -> !existingEventIds.contains(event.getGoogleEventId())) // 중복 제거
        .map(event -> CalendarDetail.builder()
            .title(event.getTitle())
            .startDatetime(event.getStart())
            .endDatetime(event.getEnd())
            .syncedAt(LocalDateTime.now())
            .isAllDay(event.isAllDay())
            .externalEtag(event.getEtag())
            .googleEventId(event.getGoogleEventId()) // 구글 이벤트 고유 ID 저장
            .calendar(member.getCalendar())
            .build()
        )
        .toList();

    // 3) 새로 생긴 이벤트만 저장
    if (!newDetails.isEmpty()) {
      googleScheduleRepository.saveAll(newDetails);
    }
  }
}
