package com.grepp.spring.app.model.mainpage.service;


import com.grepp.spring.app.model.mainpage.dto.UnifiedScheduleDto;
import com.grepp.spring.app.model.mainpage.entity.CalendarDetail;
import com.grepp.spring.app.model.mainpage.repository.GoogleScheduleRepository;
import com.grepp.spring.app.model.mainpage.repository.MainPageScheduleRepository;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarService {

  private final MainPageScheduleRepository mainPageScheduleRepository;
  private final GoogleScheduleRepository googleScheduleRepository;

  public Map<LocalDate, List<UnifiedScheduleDto>> getMonthlySchedules(String memberId,
      LocalDate anyDateInMonth) {
    // 한 달 단위로 일정 조회하기

    // 1. 월 시작/끝 구하기
    LocalDate monthStart = anyDateInMonth.withDayOfMonth(1);
    LocalDate monthEnd = anyDateInMonth.withDayOfMonth(anyDateInMonth.lengthOfMonth());

    LocalDateTime startDateTime = monthStart.atStartOfDay();
    LocalDateTime endDateTime = monthEnd.atTime(23, 59, 59);

    // 2. 내부 일정 + 구글 일정 조회 (겹침 조건)
    List<Schedule> schedules = mainPageScheduleRepository.findSchedulesForMainPage(memberId,
        startDateTime, endDateTime);
    List<CalendarDetail> googleSchedules = googleScheduleRepository.findGoogleSchedulesForMainPage(
        memberId, startDateTime, endDateTime);

    // 3. DTO 변환
    List<UnifiedScheduleDto> internalDtos = schedules.stream()
        .map(UnifiedScheduleDto::fromService)
        .toList();

    List<UnifiedScheduleDto> googleDtos = googleSchedules.stream()
        .map(UnifiedScheduleDto::fromGoogle)
        .toList();

    List<UnifiedScheduleDto> allDtos = Stream.concat(internalDtos.stream(), googleDtos.stream())
        .sorted(Comparator.comparing(UnifiedScheduleDto::getStartTime))
        .toList();

    // 4. 날짜별 그룹핑, 날짜 기준 오름차순 정렬
    return allDtos.stream()
        .collect(Collectors.groupingBy(
            dto -> dto.getStartTime().toLocalDate(),
            TreeMap::new,  // 날짜 키 오름차순 정렬
            Collectors.toList()
        ));
  }
}

