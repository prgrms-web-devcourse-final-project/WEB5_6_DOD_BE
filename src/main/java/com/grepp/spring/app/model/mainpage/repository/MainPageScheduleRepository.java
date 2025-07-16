package com.grepp.spring.app.model.mainpage.repository;

import com.grepp.spring.app.model.schedule.entity.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MainPageScheduleRepository extends JpaRepository<Schedule, Long> {

  // 1. 시작 날짜가 한 주에 걸칠 때
  // 2. 종료 날짜가 한 주에 걸칠 때
  // 3. 시작 ~ 종료 날짜가 한 주에 포함될 때

  @Query("""
    SELECT sm.schedule
    FROM ScheduleMember sm
    WHERE sm.member.id = :memberId
      AND sm.schedule.startTime < :end
      AND sm.schedule.endTime > :start
""")
  List<Schedule> findSchedulesForMainPage(
      @Param("memberId") String memberId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end
  );

}
