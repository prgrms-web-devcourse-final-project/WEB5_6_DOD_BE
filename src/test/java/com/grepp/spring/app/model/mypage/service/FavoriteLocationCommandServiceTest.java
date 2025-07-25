package com.grepp.spring.app.model.mypage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoritePlaceRequest;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.dto.FavoriteLocationDto;
import com.grepp.spring.app.model.mypage.entity.FavoriteLocation;
import com.grepp.spring.app.model.mypage.repository.MyLocationRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
// @Transactional
class FavoriteLocationCommandServiceTest {

  @Mock
  private MyLocationRepository myLocationRepository;

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private FavoriteLocationCommandService favoriteLocationCommandService;

  @Test
  void create_success() { // 장소 등록 성공
    // given -> 준비 단계
    String memberId = "member123";
    CreateFavoritePlaceRequest request = new CreateFavoritePlaceRequest("서울역", 37.55, 126.97);

    Member member = new Member();
    FavoriteLocation location = FavoriteLocation.create(member, request);

    // @BeforeEach 로 setup 메서드 만들어서 더미 멤버 만들어서 선언부 줄이기

    // 생성도 만들자 ㅜㅜ -> 리팩토링 먼저 해야굄...
    //
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    // 즐겨찾기 장소 등록 안되어있음을 가정
    when(myLocationRepository.existsByMemberId(memberId)).thenReturn(false);
    when(myLocationRepository.save(any(FavoriteLocation.class))).thenReturn(location);

    // when -> 실행 단계
    FavoriteLocationDto result = favoriteLocationCommandService.createFavoriteLocation(memberId, request);

    // then -> 검증 단계
    assertNotNull(result);
    assertEquals("서울역", result.getStationName());
    // save 1번 호출 되었는지 검증
    verify(myLocationRepository, times( 1)).save(any(FavoriteLocation.class));
  }
}