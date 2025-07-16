package com.grepp.spring.app.model.mainpage.service;

import com.grepp.spring.app.model.mainpage.repository.MainPageScheduleRepository;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainPageScheduleService { // 이때어때 내부 일정

  private final MainPageScheduleRepository mainPageScheduleRepository;

  public List<Schedule> findSchedulesInRange(String memberId, LocalDate start, LocalDate end) {
    LocalDateTime startDateTime = start.atStartOfDay();
    LocalDateTime endDateTime = end.atTime(23, 59, 59);

    return mainPageScheduleRepository.findSchedulesForMainPage(
        memberId, startDateTime, endDateTime
    );
  }
}
