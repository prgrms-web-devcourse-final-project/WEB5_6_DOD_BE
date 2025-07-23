package com.grepp.spring.app.model.mainpage.service;

import static com.grepp.spring.app.model.mainpage.dto.UnifiedScheduleDto.parseDateOrDateTime;

import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupResponse;
import com.grepp.spring.app.controller.api.mainpage.payload.response.ShowMainPageResponse;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.service.GroupQueryMainpageService;
import com.grepp.spring.app.model.mainpage.dto.UnifiedScheduleDto;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.dto.PublicCalendarEventDto;
import com.grepp.spring.app.model.mypage.service.PublicCalendarIdService;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberRepository;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainPageService { // 메인페이지 & 달력 (구글 일정 + 내부 일정 통합)

  private final GroupQueryMainpageService groupQueryMainpageService;
  private final MainPageScheduleService mainPageScheduleService;

  private final PublicCalendarService publicCalendarService;
  private final PublicCalendarIdService publicCalendarIdService;

  private final ScheduleMemberRepository scheduleMemberRepository;
  private final MemberRepository memberRepository;

  @Getter
  @AllArgsConstructor
  public static class UnifiedScheduleResult{
    private final List<UnifiedScheduleDto> schedules;
    private final boolean googleFetchSuccess;
  }

  public ShowMainPageResponse getMainPageData(String memberId, LocalDate targetDate) {

    if (memberId == null || memberId.trim().isEmpty()) {
      throw new MemberNotFoundException(MyPageErrorCode.INVALID_MEMBER_REQUEST);
    }

    // 회원 존재 여부 예외 처리
    if (!memberRepository.existsById(memberId)) {
      throw new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND);
    }

    // 그룹 정보 가져오기
    ShowGroupResponse groups = groupQueryMainpageService.displayGroup();

    // 오늘 일정
    UnifiedScheduleResult todayResult = getUnifiedSchedules(memberId, targetDate, targetDate);

    // 주간 일정 통합 , 중복 로직 해결 생각해보기
    LocalDate weekStart = targetDate.with(DayOfWeek.MONDAY); // targetDate 가 속한 주에서 월요일을 시작으로 설정
    LocalDate weekEnd = weekStart.plusDays(6);
    UnifiedScheduleResult weeklyResult = getUnifiedSchedules(memberId, weekStart, weekEnd);

    // (api 호출 한 번 실패하면 그냥 일간, 주간 둘다 false 임)
    boolean googleFetchSuccess = todayResult.isGoogleFetchSuccess();

    ShowMainPageResponse.WeeklyScheduleDto weeklyScheduleDto =
        ShowMainPageResponse.WeeklyScheduleDto.builder()
            .weekNumber(targetDate.get(WeekFields.ISO.weekOfYear())) // 해당 연도의 몇 번째 주인지 , 주간 이동 고려?
            .weekStartDate(weekStart)
            .weekEndDate(weekEnd)
            .schedules(weeklyResult.getSchedules())
            .build();

    // 최종 메인페이지 응답 생성
    return ShowMainPageResponse.builder()
        .groups(groups)
        .schedules(todayResult.getSchedules())
        .weeklySchedules(List.of(weeklyScheduleDto))
        .googleCalendarFetchSuccess(googleFetchSuccess) // 성공 여부 담기
        .build();
  }

  public UnifiedScheduleResult getUnifiedSchedules(String memberId, LocalDate start,
      LocalDate end) {

    LocalDateTime startDateTime = start.atStartOfDay();
    LocalDateTime endDateTime = end.atTime(23, 59, 59);

    log.info(">>> [getUnifiedSchedules] memberId={}, start={}, end={}",
        memberId, startDateTime, endDateTime);

    // 내부 일정 조회
    List<Schedule> schedules = mainPageScheduleService.findSchedulesInRange(memberId, start, end);

    // 우리 서비스 일정 → DTO 변환 호출
    List<UnifiedScheduleDto> internalDtos = schedules.stream()
        .map(schedule -> {
          Group group = schedule.getEvent().getGroup();
          List<ScheduleMember> participants = scheduleMemberRepository.findAllBySchedule(
              schedule);
          ScheduleMember sm = scheduleMemberRepository
              .findByScheduleIdAndMemberId(schedule.getId(), memberId)
              .orElse(null); // 참여하지 않는 일정이면 없음 처리
          return UnifiedScheduleDto.fromService(schedule, group, sm, participants);
        })
        .toList();

    // 공개 캘린더 ID 조회
    Optional<String> publicCalendarIdOpt = publicCalendarIdService.getPublicCalendarId(memberId);

    // 공개 캘린더 ID 없을 때 내부 일정만 반환
    if (publicCalendarIdOpt.isEmpty()) {
      log.info("회원 {}는 공개 캘린더 ID가 없음 → 내부 일정만 반환", memberId);
      return new UnifiedScheduleResult(internalDtos, true); // 구글 호출 자체가 없어서 true 처리
    }
    String publicCalendarId = publicCalendarIdOpt.get();

    try {

      // 공개 캘린더 일정 가져오기 -> 메인페이지 로드될 때마다 호출 (동기화 새로고침 필요 X)
      List<PublicCalendarEventDto> publicEvents = publicCalendarService.fetchPublicCalendarEvents(publicCalendarId);

      // 일정 범위 필터링하기
      publicEvents = publicEvents.stream()
          .filter(e -> {
            // 종일 -> date / 일반 -> dateTime
            LocalDateTime eventStart = parseDateOrDateTime(e.getStart());
            LocalDateTime eventEnd = parseDateOrDateTime(e.getEnd());

            if (e.isAllDay()) {
              // 종일 일정은 start 일만 조회 범위에 들어올 때 표시
              return !eventStart.isBefore(startDateTime) && !eventStart.isAfter(endDateTime);
            } else {
              // 일반 일정은 기간 겹치면 포함
              return !(eventEnd.isBefore(startDateTime) || eventStart.isAfter(endDateTime));
            }
          })
          .toList();

      // 구글 일정(calendar_detail) → DTO 변환 호출
      List<UnifiedScheduleDto> publicGoogleDtos = publicEvents.stream()
          .map(UnifiedScheduleDto::fromPublicCalendar)
          .toList();

      List<UnifiedScheduleDto> merged = Stream.concat(internalDtos.stream(), publicGoogleDtos.stream())
        .sorted(Comparator.comparing(UnifiedScheduleDto::getStartTime))
        .toList();

      return new UnifiedScheduleResult(merged, true);
    } catch (RestClientException ex) {
      log.warn("구글 공개 캘린더 조회 실패! memberId={}, publicCalendarId={}", memberId, publicCalendarId, ex);
      return new UnifiedScheduleResult(internalDtos, false); // 실패 시에도 내부 일정만 보여주도록 처리 + success 여부 false 로 처리
    }
  }
}
