package com.grepp.spring.app.model.mypage.repository;


import com.grepp.spring.app.model.mainpage.entity.Calendar;
import com.grepp.spring.app.model.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {

  Optional<Calendar> findByMember(Member member);
}
