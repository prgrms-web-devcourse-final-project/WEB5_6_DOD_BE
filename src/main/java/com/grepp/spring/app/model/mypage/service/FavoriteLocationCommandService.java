package com.grepp.spring.app.model.mypage.service;

import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoritePlaceRequest;
import com.grepp.spring.app.controller.api.mypage.payload.request.ModifyFavoritePlaceRequest;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.dto.FavoriteLocationDto;
import com.grepp.spring.app.model.mypage.entity.FavoriteLocation;
import com.grepp.spring.app.model.mypage.repository.MyLocationRepository;
import com.grepp.spring.infra.error.exceptions.mypage.FavoriteAlreadyExistException;
import com.grepp.spring.infra.error.exceptions.mypage.FavoriteNotFoundException;
import com.grepp.spring.infra.error.exceptions.mypage.FavoriteSaveFailedException;
import com.grepp.spring.infra.error.exceptions.mypage.InvalidFavoriteRequestException;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteLocationCommandService {

  private final MyLocationRepository myLocationRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public FavoriteLocationDto createFavoriteLocation(
      String memberId, CreateFavoritePlaceRequest request) {

    // INVALID_FAVORITE_REQUEST, 400  잘못된 즐겨찾기 요청 예외 처리
    if (request == null) {
      throw new InvalidFavoriteRequestException(MyPageErrorCode.INVALID_FAVORITE_REQUEST);
    }

    // 회원 존재 여부 예외 처리
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));


    // FAVORITE_ALREADY_EXISTS, 409 즐겨찾기 이미 존재 예외 처리
    if (myLocationRepository.existsByMemberId(memberId)) {
      throw new FavoriteAlreadyExistException(MyPageErrorCode.FAVORITE_ALREADY_EXISTS);
    }

    // dto -> Entity 변환 책임을 엔티티에 만들어준 팩토리 메서드로 위임
    FavoriteLocation location = FavoriteLocation.create(member,request);

    // FAVORITE_SAVE_FAILED, 500 저장 실패 예외 처리
    try {
      FavoriteLocation saved = myLocationRepository.save(location);
      // 또 다시 dto 로 변환
      return FavoriteLocationDto.fromEntity(saved);
    } catch (DataAccessException ex) {
      // DB 제약조건 위반, 연결 오류 등 저장 실패 시 여기서 처리
      throw new FavoriteSaveFailedException(MyPageErrorCode.FAVORITE_SAVE_FAILED);
    }
  }

  @Transactional
  public FavoriteLocationDto modifyFavoriteLocation(
      String memberId, ModifyFavoritePlaceRequest request) {

    if (memberId == null || memberId.trim().isEmpty()) {
      throw new MemberNotFoundException(MyPageErrorCode.INVALID_MEMBER_REQUEST);
    }
    if (request == null || request.getFavoritePlaceId() == null) {
      throw new InvalidFavoriteRequestException(MyPageErrorCode.INVALID_FAVORITE_REQUEST);
    }

    memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));

    Long locationId = request.getFavoritePlaceId();
    // 수정 대상 즐찾 존재 여부 확인
    FavoriteLocation location = myLocationRepository.findById(locationId)
        .orElseThrow(() -> new FavoriteNotFoundException(MyPageErrorCode.FAVORITE_NOT_FOUND));

    location.updateLocation(request);

    try {
      return FavoriteLocationDto.fromEntity(location); // JPA 영속성 컨텍스트 -> 자동 업데이트?
    } catch (DataAccessException e) {
      throw new FavoriteSaveFailedException(MyPageErrorCode.FAVORITE_SAVE_FAILED);
    }

  }

}
