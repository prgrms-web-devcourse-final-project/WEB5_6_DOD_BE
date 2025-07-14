package com.grepp.spring.app.model.mypage.service;

import com.grepp.spring.app.controller.api.mypage.payload.request.SetCalendarSyncRequest;
import com.grepp.spring.app.controller.api.mypage.payload.response.CalendarSyncStatusResponse;
import com.grepp.spring.app.model.mainpage.entity.Calendar;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.repository.CalendarRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarSyncService {

  private final MemberRepository memberRepository;
  private final CalendarRepository calendarRepository;

  public void updateSyncSetting(String memberId, SetCalendarSyncRequest request) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

    Calendar calendar = calendarRepository.findByMember(member)
        .orElseThrow(() -> new IllegalStateException("캘린더가 존재하지 않습니다."));

    calendar.setSynced(request.isSynced());
    calendar.setSyncedAt(LocalDateTime.now());

    calendarRepository.save(calendar); // 변경사항 저장
  }

  public CalendarSyncStatusResponse getCalendarSyncStatus(String memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

    Calendar calendar = calendarRepository.findByMember(member)
        .orElseGet(() -> createDefaultCalendar(member));

    return new CalendarSyncStatusResponse(
        calendar.getId(),
        calendar.getName(),
        calendar.getSynced()
    );
  }

  // 캘린더 생성 로직 -> 혹시 회원 탈퇴나 재가입 시 캘린더 생성 안됐을 때를 대비하여
  private Calendar createDefaultCalendar(Member member) {
    Calendar newCalendar = new Calendar();
    newCalendar.setMember(member);
    newCalendar.setName("ittaeok");
    newCalendar.setSynced(false);
    newCalendar.setSyncedAt(LocalDateTime.now());
    return calendarRepository.save(newCalendar);
  }

}
