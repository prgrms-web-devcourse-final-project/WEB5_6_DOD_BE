package com.grepp.spring.app.model.mainpage.repository;


import com.grepp.spring.app.model.mainpage.dto.UnifiedScheduleDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository {

  // 한 달 범위 일정 조회
  List<UnifiedScheduleDto> findScheduleBetween(LocalDate startDate, LocalDate endDate);

  // 특정 일자 일정 조회
  List<UnifiedScheduleDto> findSchedulesForDate(LocalDate date, String memberId);


}
