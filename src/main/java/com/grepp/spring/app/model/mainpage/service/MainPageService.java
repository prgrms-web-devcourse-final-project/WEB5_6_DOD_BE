package com.grepp.spring.app.model.mainpage.service;

import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupResponse;
import com.grepp.spring.app.controller.api.mainpage.payload.response.ShowMainPageResponse;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.service.GroupQueryMainpageService;
import com.grepp.spring.app.model.mainpage.dto.UnifiedScheduleDto;
import com.grepp.spring.app.model.mainpage.entity.CalendarDetail;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberRepository;
import com.grepp.spring.infra.error.exceptions.mypage.InvalidFavoriteRequestException;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainPageService { // 메인페이지 & 달력 (구글 일정 + 내부 일정 통합)

  private final GroupQueryMainpageService groupQueryMainpageService;
  private final MainPageScheduleService mainPageScheduleService;
  private final GoogleScheduleService googleScheduleService;

  private final ScheduleMemberRepository scheduleMemberRepository;
  private final MemberRepository memberRepository;

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

    // 오늘 일정 통합
    List<UnifiedScheduleDto> todaySchedules = getUnifiedSchedules(memberId, targetDate, targetDate);

    // 주간 일정 통합
    LocalDate weekStart = targetDate.with(DayOfWeek.MONDAY); // targetDate 가 속한 주에서 월요일을 시작으로 설정
    LocalDate weekEnd = weekStart.plusDays(6);
    List<UnifiedScheduleDto> weeklySchedules = getUnifiedSchedules(memberId, weekStart, weekEnd);

    log.info(">>> [메인페이지] memberId={}, targetDate={}, todaySchedules={}, weeklySchedules={}",
        memberId, targetDate, todaySchedules.size(), weeklySchedules.size());

    ShowMainPageResponse.WeeklyScheduleDto weeklyScheduleDto =
        ShowMainPageResponse.WeeklyScheduleDto.builder()
            .weekNumber(targetDate.get(WeekFields.ISO.weekOfYear())) // 해당 연도의 몇 번째 주인지 , 주간 이동 고려?
            .weekStartDate(weekStart)
            .weekEndDate(weekEnd)
            .schedules(weeklySchedules)
            .build();

    // 최종 메인페이지 응답 생성
    return ShowMainPageResponse.builder()
        .groups(groups)
        .schedules(todaySchedules)
        .weeklySchedules(List.of(weeklyScheduleDto))
        .build();
  }

  public List<UnifiedScheduleDto> getUnifiedSchedules(String memberId, LocalDate start, LocalDate end) {

    LocalDateTime startDateTime = start.atStartOfDay();
    LocalDateTime endDateTime = end.atTime(23, 59, 59);

    log.info(">>> [getUnifiedSchedules] memberId={}, start={}, end={}",
        memberId, startDateTime, endDateTime);


    List<Schedule> schedules = mainPageScheduleService.findSchedulesInRange(memberId, start, end);
    List<CalendarDetail> googleSchedules = googleScheduleService.findSchedulesInRange(memberId, start, end);

    log.info(">>> 내부 일정 개수={}, 구글 일정 개수={}",
        schedules.size(), googleSchedules.size());

    // 우리 서비스 일정 → DTO 변환 호출
    List<UnifiedScheduleDto> internalDtos = schedules.stream()
        .map(schedule -> {
          Group group = schedule.getEvent().getGroup();
          // 6List<GroupMember> groupMembers = group.getGroupMembers();
          List<ScheduleMember> participants = scheduleMemberRepository.findAllBySchedule(schedule);
          ScheduleMember sm = scheduleMemberRepository
              .findByScheduleIdAndMemberId(schedule.getId(), memberId)
              .orElse(null); // 참여하지 않는 일정이면 없음 처리

          return UnifiedScheduleDto.fromService(schedule, group, sm, participants);
        })
        .toList();

    // 구글 일정(calendar_detail) → DTO 변환 호출
    List<UnifiedScheduleDto> googleDtos = googleSchedules.stream()
        .map(UnifiedScheduleDto::fromGoogle)
        .toList();

    // dto 리스트끼리 합치고 시간순 정렬
    return Stream.concat(internalDtos.stream(), googleDtos.stream())
        .sorted(Comparator.comparing(UnifiedScheduleDto::getStartTime))
        .toList();
  }
}
