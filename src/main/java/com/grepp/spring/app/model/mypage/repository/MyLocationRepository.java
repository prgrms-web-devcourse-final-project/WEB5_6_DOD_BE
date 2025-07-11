package com.grepp.spring.app.model.mypage.repository;

import com.grepp.spring.app.model.mypage.entity.FavoriteLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyLocationRepository extends JpaRepository<FavoriteLocation, Long> {


  // 즐찾 장소 존재 여부 확인
  boolean existsByMemberId(String memberId);
}
