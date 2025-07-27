package com.grepp.spring.app.model.mypage.service;


import com.grepp.spring.app.controller.api.mypage.payload.response.PublicCalendarIdResponse;
import com.grepp.spring.app.model.mainpage.entity.Calendar;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.repository.CalendarRepository;
import com.grepp.spring.infra.error.exceptions.mypage.InvalidPublicCalendarIdException;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.error.exceptions.mypage.PublicCalendarIdNotFoundException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PublicCalendarIdService {

  private final CalendarRepository calendarRepository;
  private final MemberRepository memberRepository;

  // 공개 캘린더 ID 저장
  @Transactional
  public void savePublicCalendarId(String memberId, String publicCalendarId) {

    // null / 빈 문자열 저장 차단
    if (publicCalendarId == null || publicCalendarId.trim().isEmpty()) {
      throw new InvalidPublicCalendarIdException(MyPageErrorCode.INVALID_PUBLIC_CALENDAR_ID);
    }

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));

    Calendar calendar = calendarRepository.findByMember(member)
        .orElseThrow(() -> new IllegalStateException("회원의 캘린더가 존재하지 않습니다."));

    calendar.setPublicCalendarId(publicCalendarId);
    calendarRepository.save(calendar);
  }

  // DB 에 저장된 공개 캘린더 ID 조회
  @Transactional(readOnly = true)
  public Optional<String> getPublicCalendarId(String memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));

    return calendarRepository.findByMember(member)
        .map(Calendar::getPublicCalendarId)
        .filter(id -> id != null && !id.isBlank()); // null/빈 값이면 Optional.empty()
  }


  @Transactional
  public void deletePublicCalendarId(String memberId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));

    Calendar calendar = calendarRepository.findByMember(member)
        .orElseThrow(() -> new IllegalStateException("회원의 캘린더가 존재하지 않습니다."));

    if (calendar.getPublicCalendarId() == null || calendar.getPublicCalendarId().isBlank()) {
      throw new PublicCalendarIdNotFoundException(MyPageErrorCode.PUBLIC_CALENDAR_ID_NOT_FOUND);
    }

    calendar.setPublicCalendarId(null);
    calendarRepository.save(calendar);
  }
}
