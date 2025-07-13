package com.grepp.spring.app.model.mypage.repository;

import com.grepp.spring.app.model.mypage.entity.FavoriteTimetable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MyTimetableRepository extends JpaRepository<FavoriteTimetable, Long> {

  List<FavoriteTimetable> findAllByMemberId(String memberId);

  Optional<FavoriteTimetable> findByMemberIdAndDay(String memberId, String day);

  void deleteByMemberIdAndDay(String memberId, String day);


//  @Query("""
//    SELECT ft
//    FROM FavoriteTimetable ft
//    WHERE ft.member.id = :memberId
//      AND ft.weekday = :weekday
//      AND ft.startTime < :endTime
//      AND ft.endTime > :startTime
//""")
//  List<FavoriteTimetable> findOverlappingTimetables(
//      @Param("memberId") String memberId,
//      @Param("weekday") DayOfWeek weekday,
//      @Param("startTime") LocalTime startTime,
//      @Param("endTime") LocalTime endTime
//  );

}
