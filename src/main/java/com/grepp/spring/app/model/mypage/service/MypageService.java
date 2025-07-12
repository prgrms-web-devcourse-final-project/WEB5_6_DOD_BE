package com.grepp.spring.app.model.mypage.service;


import static com.grepp.spring.app.model.mypage.dto.FavoriteLocationDto.toEntity;

import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoritePlaceRequest;
import com.grepp.spring.app.controller.api.mypage.payload.request.ModifyFavoritePlaceRequest;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.dto.FavoriteLocationDto;
import com.grepp.spring.app.model.mypage.dto.FavoriteTimetableDto;
import com.grepp.spring.app.model.mypage.entity.FavoriteLocation;
import com.grepp.spring.app.model.mypage.entity.FavoriteTimetable;
import com.grepp.spring.app.model.mypage.repository.MyLocationRepository;
import com.grepp.spring.app.model.mypage.repository.MyTimetableRepository;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import java.util.ArrayList;
import java.util.List;
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

//  @Transactional
//  public FavoriteTimetableDto createFavoriteTimetable(
//      String memberId, CreateFavoriteTimeRequest request) {
//
//    if (memberId == null || memberId.trim().isEmpty()) {
//      throw new IllegalArgumentException("memberId는 필수입니다.");
//    }
//
//    if (request == null) {
//      throw new IllegalArgumentException("요청 정보가 없습니다.");
//    }
//
//    Member member = memberRepository.findById(memberId)
//        .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
//
//    // 겹치는 시간대 존재 여부 확인
//    boolean hasOverlap = myTimetableRepository.existsOverlappingTime(
//        memberId,
//        request.getWeekday(),
//        request.getStartTime(),
//        request.getEndTime()
//    );
//
//    if (hasOverlap) {
//      throw new IllegalStateException("겹치는 시간대가 이미 존재합니다.");
//    }
//
//    FavoriteTimetableDto dto = FavoriteTimetableDto.toDto(request, member);
//
//    FavoriteTimetable entity = toEntity(dto);
//
//    FavoriteTimetable saved = myTimetableRepository.save(entity);
//
//    // TODO: 중복 또는 겹치는 요일 및 시간대 저장 예외 처리
//
//    return FavoriteTimetableDto.fromEntity(saved);
//  }

  public List<FavoriteLocationDto> getFavoriteLocations(String memberId) {
    List<FavoriteLocation> locations = myLocationRepository.findAllByMemberId(memberId);
    List<FavoriteLocationDto> result = new ArrayList<>();

    for (FavoriteLocation location : locations) {
      // 각 FavoriteLocation을 FavoriteLocationDto로 변환하여 result 리스트에 추가
      FavoriteLocationDto dto = FavoriteLocationDto.fromEntity(location);
      result.add(dto);
    }
    return result;
  }

  public List<FavoriteTimetableDto> getFavoriteTimetables(String memberId) {
    List<FavoriteTimetable> timetables = myTimetableRepository.findAllByMemberId(memberId);
    List<FavoriteTimetableDto> result = new ArrayList<>();

    for (FavoriteTimetable timetable : timetables) {
      FavoriteTimetableDto dto = FavoriteTimetableDto.fromEntity(timetable);
      result.add(dto);
    }
    return result;

  }

  public FavoriteLocationDto modifyFavoriteLocation(
      String memberId, ModifyFavoritePlaceRequest request) {

    Long locationId = request.getFavoritePlaceId();
    FavoriteLocation location = myLocationRepository.findById(locationId)
        .orElseThrow(() -> new NotFoundException("즐겨찾기 장소를 찾을 수 없습니다."));


    location.setName(request.getStationName());
    location.setLatitude(request.getLatitude());
    location.setLongitude(request.getLongitude());

    FavoriteLocation updated = myLocationRepository.save(location);

    return FavoriteLocationDto.fromEntity(updated);

  }

}
