package com.grepp.spring.app.model.mypage.service;

import com.grepp.spring.app.controller.api.mypage.payload.response.CreateFavoriteTimeResponse;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.converter.FavoriteTimetableConverter;
import com.grepp.spring.app.model.mypage.dto.FavoriteTimetableDto;
import com.grepp.spring.app.model.mypage.repository.MyTimetableRepository;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteTimetableQueryService {

  private final MyTimetableRepository myTimetableRepository;
  private final MemberRepository memberRepository;


  // dto 리스트 가져오기 -> Map<day, hexString> 변환 (hex 값으로 리턴할 때 사용)
  // 실 조회를 위해 호출되는 것

  public CreateFavoriteTimeResponse getFavoriteTimetableResponse(String memberId) {
    if (memberId == null || memberId.trim().isEmpty()) {
      throw new MemberNotFoundException(MyPageErrorCode.INVALID_MEMBER_REQUEST);
    }

    memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));

    List<FavoriteTimetableDto> dtos = getFavoriteTimetables(memberId);

    Map<String, String> dayToBitMap = FavoriteTimetableConverter.toDayBitMap(dtos);

    // 리스트 없으면 404 아니고 그냥 빈 데이터 반환
    return FavoriteTimetableDto.fromDto(dayToBitMap);
  }

  // FavoriteTimetable 엔티티 -> FavoriteTimetableDto 로 변환 & 리스트로 반환
  public List<FavoriteTimetableDto> getFavoriteTimetables(String memberId) {

    return myTimetableRepository.findAllByMemberId(memberId)
        .stream()
        .map(FavoriteTimetableDto::fromEntity)
        .collect(Collectors.toList());
  }


}
