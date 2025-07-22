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

  // 조회 구간과 일정이 한 번이라도 겹치면 포함
  // = 제외한 이유: all-day 일정의 end 는 다음날 00:00 으로 잡혀서 다음날까지 중복 조회됨

  @Query("""
    SELECT DISTINCT s
    FROM Schedule s
    JOIN s.scheduleMembers sm
    JOIN FETCH s.event e
    LEFT JOIN FETCH e.group g
    LEFT JOIN FETCH g.groupMembers gm
    LEFT JOIN FETCH gm.member m
    WHERE sm.member.id = :memberId
      AND s.startTime < :end
      AND s.endTime > :start
""")
  List<Schedule> findSchedulesForMainPage(
      @Param("memberId") String memberId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end
  );

}
