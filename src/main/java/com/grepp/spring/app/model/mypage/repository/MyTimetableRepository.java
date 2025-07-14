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

}
