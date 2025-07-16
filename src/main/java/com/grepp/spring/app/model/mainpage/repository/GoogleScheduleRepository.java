package com.grepp.spring.app.model.mainpage.repository;

import com.grepp.spring.app.model.mainpage.entity.CalendarDetail;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GoogleScheduleRepository extends JpaRepository<CalendarDetail, Long> {

  @Query("""
SELECT g FROM CalendarDetail g
WHERE g.calendar.member.id = :memberId
  AND g.startDatetime < :end
  AND g.endDatetime > :start
""")
  List<CalendarDetail> findGoogleSchedulesForMainPage(
      @Param("memberId") String memberId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end
  );

  // 중복 체크
  @Query("SELECT g.googleEventId FROM CalendarDetail g WHERE g.calendar.id = :calendarId")
  List<String> findGoogleEventIdsByCalendar(@Param("calendarId") Long calendarId);
}
