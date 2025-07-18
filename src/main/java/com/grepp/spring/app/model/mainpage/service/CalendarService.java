package com.grepp.spring.app.model.mainpage.service;

import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.mainpage.dto.UnifiedScheduleDto;
import com.grepp.spring.app.model.mainpage.entity.CalendarDetail;
import com.grepp.spring.app.model.mainpage.repository.GoogleScheduleRepository;
import com.grepp.spring.app.model.mainpage.repository.MainPageScheduleRepository;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberRepository;
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
  private final ScheduleMemberRepository scheduleMemberRepository;

  public Map<LocalDate, List<UnifiedScheduleDto>> getMonthlySchedules(
      String memberId,
      LocalDate startDate,
      LocalDate endDate
  ) {
    // 한 달 단위로 일정 조회하기

    // 1. 월 시작/끝 구하기
    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

    // 2. 내부 일정 + 구글 일정 조회 (겹침 조건)
    List<Schedule> schedules = mainPageScheduleRepository.findSchedulesForMainPage(memberId,
        startDateTime, endDateTime);
    List<CalendarDetail> googleSchedules = googleScheduleRepository.findGoogleSchedulesForMainPage(
        memberId, startDateTime, endDateTime);

    // 3. DTO 변환
    List<UnifiedScheduleDto> internalDtos = schedules.stream()
        .map(schedule -> {
          Group group = schedule.getEvent().getGroup();
          List<GroupMember> groupMembers = group.getGroupMembers();
          ScheduleMember sm = scheduleMemberRepository
              .findByScheduleIdAndMemberId(schedule.getId(), memberId)
              .orElseThrow(() ->
                  new IllegalStateException("해당 일정에 대한 ScheduleMember가 존재하지 않습니다. scheduleId=" + schedule.getId())
              );
          return UnifiedScheduleDto.fromService(schedule, group, sm ,groupMembers);
        })
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

