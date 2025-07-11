package com.grepp.spring.app.model.mypage.service;


import static com.grepp.spring.app.model.mypage.dto.FavoriteLocationDto.toEntity;
import static com.grepp.spring.app.model.mypage.dto.FavoriteTimetableDto.toEntity;

import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoritePlaceRequest;
import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoriteTimeRequest;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.dto.FavoriteLocationDto;
import com.grepp.spring.app.model.mypage.dto.FavoriteTimetableDto;
import com.grepp.spring.app.model.mypage.entity.FavoriteLocation;
import com.grepp.spring.app.model.mypage.entity.FavoriteTimetable;
import com.grepp.spring.app.model.mypage.repository.MyLocationRepository;
import com.grepp.spring.app.model.mypage.repository.MyTimetableRepository;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MypageService {

  private final MemberRepository memberRepository;
  private final MyLocationRepository myLocationRepository;
  private final MyTimetableRepository myTimetableRepository;


  @Transactional
  public FavoriteLocationDto  createFavoriteLocation(
      String memberId, CreateFavoritePlaceRequest request) {

    if (memberId == null || memberId.trim().isEmpty()) {
      throw new IllegalArgumentException("memberId는 필수입니다.");
    }

    if (request == null) {
      throw new IllegalArgumentException("요청 정보가 없습니다.");
    }

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

    FavoriteLocationDto dto = FavoriteLocationDto.toDto(request, member);

    if(myLocationRepository.existsByMemberId(dto.getMemberId())){
      throw new IllegalStateException("이미 즐겨찾기 장소를 등록했습니다.");
    }

    // Entity 로 변환
    FavoriteLocation entity = toEntity(dto);

    // 저장하기
    FavoriteLocation saved = myLocationRepository.save(entity);

    // 또 다시 dto 로 변환
    return FavoriteLocationDto.fromEntity(saved);
  }

  @Transactional
  public FavoriteTimetableDto createFavoriteTimetable(
      String memberId, CreateFavoriteTimeRequest request) {

    if (memberId == null || memberId.trim().isEmpty()) {
      throw new IllegalArgumentException("memberId는 필수입니다.");
    }

    if (request == null) {
      throw new IllegalArgumentException("요청 정보가 없습니다.");
    }

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

    FavoriteTimetableDto dto = FavoriteTimetableDto.toDto(request, member);

    FavoriteTimetable entity = toEntity(dto);

    FavoriteTimetable saved = myTimetableRepository.save(entity);

    return FavoriteTimetableDto.fromEntity(saved);
  }

}
