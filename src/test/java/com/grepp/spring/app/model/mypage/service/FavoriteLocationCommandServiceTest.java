package com.grepp.spring.app.model.mypage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grepp.spring.app.controller.api.auth.Provider;
import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoritePlaceRequest;
import com.grepp.spring.app.controller.api.mypage.payload.request.ModifyFavoritePlaceRequest;
import com.grepp.spring.app.model.member.code.Role;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.entity.FavoriteLocation;
import com.grepp.spring.app.model.mypage.repository.MyLocationRepository;
import com.grepp.spring.infra.error.exceptions.mypage.FavoriteAlreadyExistException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FavoriteLocationCommandServiceTest {

  @Mock
  private MyLocationRepository myLocationRepository;

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private FavoriteLocationCommandService favoriteLocationCommandService;

  Member dummyMember1;
//  Member dummyMember2;
  FavoriteLocation dummyFavoriteLocation1;


  @BeforeEach
  void setUp() {
    dummyMember1 = new Member();
    dummyMember1.setId("GOOGLE_123456");
    dummyMember1.setName("하명도 ");
    dummyMember1.setProvider(Provider.GOOGLE);
    dummyMember1.setEmail("hmd@example.com");
    dummyMember1.setRole(Role.ROLE_USER);
    dummyMember1.setProfileImageNumber(2);
    dummyMember1.setTel("010-8888-9999");

//    dummyMember2 = new Member();
//    dummyMember2.setId("GOOGLE_7890");
//    dummyMember2.setName("자걸희 ");
//    dummyMember2.setProvider(Provider.GOOGLE);
//    dummyMember2.setEmail("jgh@example.com");
//    dummyMember2.setRole(Role.ROLE_USER);
//    dummyMember2.setProfileImageNumber(4);
//    dummyMember2.setTel("010-2222-9999");

    dummyFavoriteLocation1 = new FavoriteLocation();
    dummyFavoriteLocation1.setId(1L);
    dummyFavoriteLocation1.setLatitude(37.4979);
    dummyFavoriteLocation1.setLongitude(127.0276);
    dummyFavoriteLocation1.setName("강남역");
  }

  @Nested
  @DisplayName("즐겨찾기 장소 생성, 수정 테스트")
  class CreateFavLocationTest{

    @Test
    @DisplayName("성공: 회원이 기존에 등록한 즐겨찾기 장소가 없고 신규 등록.")
    void create_success() { // 장소 등록 성공
      // given -> 준비 단계
      String memberId = dummyMember1.getId();
      CreateFavoritePlaceRequest request =
          new CreateFavoritePlaceRequest("서울역", 37.55, 126.97);

      FavoriteLocation location = FavoriteLocation.create(dummyMember1, request);

      when(memberRepository.findById(memberId)).thenReturn(Optional.of(dummyMember1));
      // 즐겨찾기 장소 등록 안되어있음을 가정
      when(myLocationRepository.existsByMemberId(memberId)).thenReturn(false);
      when(myLocationRepository.save(any(FavoriteLocation.class))).thenReturn(location);

      // when -> 실행 단계
      var result = favoriteLocationCommandService.createFavoriteLocation(memberId, request);

      // then -> 검증 단계
      assertNotNull(result);
      assertEquals("서울역", result.getStationName());
      // save 1번 호출 되었는지 검증
      verify(myLocationRepository, times( 1)).save(any(FavoriteLocation.class));
    }

    @Test
    @DisplayName("실패: 회원이 기존에 등록한 즐겨찾기 장소가 존재하면 FavoriteAlreadyExistException 발생")
    void create_fail_alreadyExists() { // 장소 등록 성공
      // given -> 준비 단계
      String memberId = dummyMember1.getId();
      CreateFavoritePlaceRequest request =
          new CreateFavoritePlaceRequest("서울역", 37.55, 126.97);

      // 회원은 정상 조회
      when(memberRepository.findById(memberId)).thenReturn(Optional.of(dummyMember1));
      // 이미 즐겨찾기 장소 등록 되어있다고 가정. (true 면 바로 예외 던짐)
      when(myLocationRepository.existsByMemberId(memberId)).thenReturn(true);

      // when & then
      assertThrows(FavoriteAlreadyExistException.class,
          () -> favoriteLocationCommandService.createFavoriteLocation(memberId, request));

      // save 호출 안됐는지 검증
      verify(myLocationRepository, never()).save(any(FavoriteLocation.class));
    }
  }

  @Nested
  @DisplayName("즐겨찾기 장소 수정 테스트")
  class updateFavLocationTest{

    @Test
    @DisplayName("성공: 즐겨찾기 장소 수정")
    void update_favLocation_success(){

      // given
      String memberId = dummyMember1.getId();

      ModifyFavoritePlaceRequest request =
          new ModifyFavoritePlaceRequest(1L,"합정역", 37.5492, 126.9135);

      // 즐찾 데이터 + 회원 셋팅
      dummyFavoriteLocation1.setMember(dummyMember1);

      when(memberRepository.findById(memberId)).thenReturn(Optional.of(dummyMember1));
      

      // 기존 꺼 조회
      when(myLocationRepository.findById(1L)).thenReturn(Optional.of(dummyFavoriteLocation1));

      // when
      var result = favoriteLocationCommandService.modifyFavoriteLocation(memberId, request);

      // then
      assertNotNull(result);
      assertEquals("합정역", result.getStationName());
      assertEquals(37.5492, result.getLatitude());
      assertEquals(126.9135, result.getLongitude());

      verify(memberRepository, times(1)).findById(memberId);
      verify(myLocationRepository, times(1)).findById(1L);
      // save() 호출되지 않는지 확인. 서비스에서 호출안하고 자동 업데이트
      verify(myLocationRepository, never()).save(any(FavoriteLocation.class));
    }

    @Test
    @DisplayName("실패: 수정 요청 시에 사전에 등록된 즐겨찾기 장소 없을 때")
    void update_favLocation_fail(){

//      // given
//      String memberId = dummyMember1.getId();
//
//      ModifyFavoritePlaceRequest request =
//          new ModifyFavoritePlaceRequest(1L,"합정역", 37.5492, 126.9135);
//
//      // 즐찾 데이터 + 회원 셋팅
//      dummyFavoriteLocation1.setMember(dummyMember1);
//
//      when(memberRepository.findById(memberId)).thenReturn(Optional.of(dummyMember1));
//
//      // 기존 꺼 조회
//      when(myLocationRepository.findById(1L)).thenReturn(Optional.of(dummyFavoriteLocation1));
//
//      // when
//      var result = favoriteLocationCommandService.modifyFavoriteLocation(memberId, request);
//
//      // then
//      assertNotNull(result);
//      assertEquals("합정역", result.getStationName());
//      assertEquals(37.5492, result.getLatitude());
//      assertEquals(126.9135, result.getLongitude());
//
//      verify(memberRepository, times(1)).findById(memberId);
//      verify(myLocationRepository, times(1)).findById(1L);
//      // save() 호출되지 않는지 확인. 서비스에서 호출안하고 자동 업데이트
//      verify(myLocationRepository, never()).save(any(FavoriteLocation.class));
    }

  }


}