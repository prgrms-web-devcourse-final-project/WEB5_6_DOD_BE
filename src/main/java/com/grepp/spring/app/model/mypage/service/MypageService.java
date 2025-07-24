package com.grepp.spring.app.model.mypage.service;


import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoritePlaceRequest;
import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoriteTimeRequest;
import com.grepp.spring.app.controller.api.mypage.payload.request.ModifyFavoritePlaceRequest;
import com.grepp.spring.app.controller.api.mypage.payload.response.CreateFavoriteTimeResponse;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.dto.FavoriteLocationDto;
import com.grepp.spring.app.model.mypage.dto.FavoriteTimetableDto;
import com.grepp.spring.app.model.mypage.entity.FavoriteLocation;
import com.grepp.spring.app.model.mypage.entity.FavoriteTimetable;
import com.grepp.spring.app.model.mypage.repository.MyLocationRepository;
import com.grepp.spring.app.model.mypage.repository.MyTimetableRepository;
import com.grepp.spring.infra.error.exceptions.mypage.FavoriteAlreadyExistException;
import com.grepp.spring.infra.error.exceptions.mypage.FavoriteNotFoundException;
import com.grepp.spring.infra.error.exceptions.mypage.FavoriteSaveFailedException;
import com.grepp.spring.infra.error.exceptions.mypage.InvalidFavoriteRequestException;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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
    // FAVORITE_FORBIDDEN 403 -> 이것도 그냥 컨트롤러에서 토큰 비교로 막기?

    // FAVORITE_NOT_FOUND 404 -> 하지 않고 빈 리스트 반환. 그럼 필요 없을 듯
    return result;
  }

  @Transactional
  public  CreateFavoriteTimeResponse createOrUpdateFavoriteTimetable(String memberId, CreateFavoriteTimeRequest request) {

    if (memberId == null || memberId.trim().isEmpty()) {
      throw new MemberNotFoundException(MyPageErrorCode.INVALID_MEMBER_REQUEST);
    }

    // INVALID_FAVORITE_REQUEST, 400  잘못된 즐겨찾기 요청 예외 처리
    if (request == null) {
      throw new InvalidFavoriteRequestException(MyPageErrorCode.INVALID_FAVORITE_REQUEST);
    }

    // 회원 존재 여부 예외 처리
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));

    // 요일 배열 -> DayOfWeek 사용할지 말지?
    String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
    List<FavoriteTimetableDto> resultList = new ArrayList<>();

    for (String day : days) {
      // 요청에서 블럭 클릭한 것 16진수 문자열로 가져오기
      String newBitHex = getTimeBitByDay(request, day);
      // 해당 요일 선택 블럭없다면 건너뜀
      if (newBitHex == null || newBitHex.trim().isEmpty())
        continue;

      // 16진수 문자열 -> 10진수 Long 값 변환 (for XOR 연산)
      Long newBitLong = Long.parseUnsignedLong(newBitHex, 16);

      // 기존 데이터 조회 or 새로 생성
      FavoriteTimetable timetable = myTimetableRepository
          .findByMemberIdAndDay(memberId, day.toUpperCase())
          .orElse(FavoriteTimetable.create(member, day, 0L));

      // XOR 로직 위임
      boolean remains = timetable.toggle(newBitLong);

      if (!remains) {
        myTimetableRepository.delete(timetable);
      } else {
        myTimetableRepository.save(timetable);
        resultList.add(FavoriteTimetableDto.fromEntity(timetable));
      }
    }

    // 응답 구조 dto 로 변환, 16진수로 포맷
    Map<String, String> dayToBitMap = resultList.stream()
        .collect(Collectors.toMap(
            FavoriteTimetableDto::getDay,
            dto -> String.format("%012X", dto.getTimeBit()),
            (existing, replacement) -> replacement // 중복되면 마지막 값으로 덮어쓰기 (중복 key 에러 방지)
        ));

    return FavoriteTimetableDto.fromDto(dayToBitMap);
  }

  // FavoriteTimetable 엔티티 -> FavoriteTimetableDto 로 변환 & 리스트로 반환
  public List<FavoriteTimetableDto> getFavoriteTimetables(String memberId) {
    List<FavoriteTimetable> timetables = myTimetableRepository.findAllByMemberId(memberId);

    return timetables.stream()
        .map(FavoriteTimetableDto::fromEntity)
        .collect(Collectors.toList());
  }
  // 요청값에서 받은 요일별 timeBit 16진수 값 requestDto 에서 꺼내주기

  private String getTimeBitByDay(CreateFavoriteTimeRequest req, String day) {
    return switch (day) {
      case "MON" -> req.getTimeBitMon();
      case "TUE" -> req.getTimeBitTue();
      case "WED" -> req.getTimeBitWed();
      case "THU" -> req.getTimeBitThu();
      case "FRI" -> req.getTimeBitFri();
      case "SAT" -> req.getTimeBitSat();
      case "SUN" -> req.getTimeBitSun();
      default -> null;
    };
  }

  // dto 리스트 가져오기 -> Map<day, hexString> 변환 (hex 값으로 리턴할 때 사용)
  // 실 조회를 위해 호출되는 것
  public CreateFavoriteTimeResponse getFavoriteTimetableResponse(String memberId) {
    if (memberId == null || memberId.trim().isEmpty()) {
      throw new MemberNotFoundException(MyPageErrorCode.INVALID_MEMBER_REQUEST);
    }

    memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(MyPageErrorCode.MEMBER_NOT_FOUND));

    List<FavoriteTimetableDto> dtos = getFavoriteTimetables(memberId);

    Map<String, String> dayToBitMap = dtos.stream()
        .collect(Collectors.toMap(
            FavoriteTimetableDto::getDay,
            dto -> String.format("%012X", dto.getTimeBit()) // 10진수 -> 16진수로 바꾸기
        ));

    // 리스트 없으면 404 아니고 그냥 빈 데이터 반환
    return FavoriteTimetableDto.fromDto(dayToBitMap);
  }

}