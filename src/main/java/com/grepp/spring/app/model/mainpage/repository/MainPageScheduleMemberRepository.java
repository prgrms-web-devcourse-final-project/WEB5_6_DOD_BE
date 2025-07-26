package com.grepp.spring.app.model.mainpage.repository;

import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainPageScheduleMemberRepository extends JpaRepository<ScheduleMember, Long> {

}
