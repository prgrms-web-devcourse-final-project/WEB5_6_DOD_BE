package com.grepp.spring.app.model.mainpage.service;

import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupResponse;
import com.grepp.spring.app.controller.api.mainpage.payload.response.ShowMainPageResponse;
import com.grepp.spring.app.model.group.service.GroupQueryService;
import com.grepp.spring.app.model.mainpage.code.ScheduleSource;
import com.grepp.spring.app.model.mainpage.dto.UnifiedScheduleDto;
import com.grepp.spring.app.model.mainpage.entity.CalendarDetail;
import com.grepp.spring.app.model.mainpage.repository.GoogleScheduleRepository;
import com.grepp.spring.app.model.mainpage.repository.MainPageScheduleRepository;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.service.ScheduleQueryService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainPageService { // 메인페이지 & 달력 (구글 일정 + 내부 일정 통합)

  private final GroupQueryService groupQueryService;
  private final ScheduleQueryService scheduleQueryService;
  private final MainPageScheduleService mainPageScheduleService;
  private final MainPageScheduleRepository mainPageScheduleRepository;
  private final GoogleScheduleService googleScheduleService;

  public ShowMainPageResponse getMainPageData(String memberId, LocalDate targetDate) {

    // 그룹 정보 가져오기
    ShowGroupResponse groups = groupQueryService.displayGroup();

    // 오늘 일정 통합
    List<UnifiedScheduleDto> todaySchedules = getUnifiedSchedules(memberId, targetDate, targetDate);

    // 주간 일정 통합
    LocalDate weekStart = targetDate.with(DayOfWeek.MONDAY); // targetDate 가 속한 주에서 월요일을 시작으로 설정
    LocalDate weekEnd = weekStart.plusDays(6);
    List<UnifiedScheduleDto> weeklySchedules = getUnifiedSchedules(memberId, weekStart, weekEnd);

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

  private List<UnifiedScheduleDto> getUnifiedSchedules(String memberId, LocalDate start, LocalDate end) {

    List<Schedule> schedules = mainPageScheduleService.findSchedulesInRange(memberId, start, end);
    List<CalendarDetail> googleSchedules = googleScheduleService.findSchedulesInRange(memberId, start, end);

    // 우리 서비스 일정 → DTO 변환 호출
    List<UnifiedScheduleDto> internalDtos = schedules.stream()
        .map(UnifiedScheduleDto::fromService)
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
