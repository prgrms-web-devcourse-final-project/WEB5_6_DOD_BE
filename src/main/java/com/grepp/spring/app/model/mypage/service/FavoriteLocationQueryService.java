package com.grepp.spring.app.model.mypage.service;

import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.dto.FavoriteLocationDto;
import com.grepp.spring.app.model.mypage.entity.FavoriteLocation;
import com.grepp.spring.app.model.mypage.repository.MyLocationRepository;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteLocationQueryService {

  private final MyLocationRepository myLocationRepository;
  private final MemberRepository memberRepository;

  public List<FavoriteLocationDto> getFavoriteLocations(String memberId) {
    List<FavoriteLocation> locations = myLocationRepository.findAllByMemberId(memberId);
    List<FavoriteLocationDto> result = new ArrayList<>();

    // MEMBER_NOT_FOUND 404
    memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND)); // 404

    for (FavoriteLocation location : locations) {
      // 각 FavoriteLocation을 FavoriteLocationDto로 변환하여 result 리스트에 추가
      FavoriteLocationDto dto = FavoriteLocationDto.fromEntity(location);
      result.add(dto);
    }
    return result;
  }

}
