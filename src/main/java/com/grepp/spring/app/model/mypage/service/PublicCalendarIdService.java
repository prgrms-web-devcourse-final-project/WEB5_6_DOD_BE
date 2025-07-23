package com.grepp.spring.app.model.mypage.service;


import com.grepp.spring.app.model.mainpage.entity.Calendar;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.repository.CalendarRepository;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
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
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));

    Calendar calendar = calendarRepository.findByMember(member)
        .orElseThrow(() -> new IllegalStateException("회원의 캘린더가 존재하지 않습니다."));

    calendar.setPublicCalendarId(publicCalendarId);
    calendarRepository.save(calendar);
  }

  // 공개 캘린더 ID 로직에서 조회
  @Transactional(readOnly = true)
  public Optional<String> getPublicCalendarId(String memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));

    return calendarRepository.findByMember(member)
        .map(Calendar::getPublicCalendarId);
  }

}
