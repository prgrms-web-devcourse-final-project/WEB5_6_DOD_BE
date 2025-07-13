package com.grepp.spring.app.model.mypage.service;


import static com.grepp.spring.app.model.mypage.dto.FavoriteLocationDto.toEntity;

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
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
  public FavoriteLocationDto createFavoriteLocation(
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

    if (myLocationRepository.existsByMemberId(dto.getMemberId())) {
      throw new IllegalStateException("이미 즐겨찾기 장소를 등록했습니다.");
    }

    // Entity 로 변환
    FavoriteLocation entity = toEntity(dto);

    // 저장하기
    FavoriteLocation saved = myLocationRepository.save(entity);

    // 또 다시 dto 로 변환
    return FavoriteLocationDto.fromEntity(saved);
  }

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

  @Transactional
  public  List<FavoriteTimetableDto> createOrUpdateFavoriteTimetable(String memberId, CreateFavoriteTimeRequest request) {

    if (memberId == null || memberId.trim().isEmpty()) {
      throw new IllegalArgumentException("memberId는 필수입니다.");
    }

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

    if (request == null) {
      throw new IllegalArgumentException("요청 정보가 없습니다.");
    }

    String[] days = {"mon", "tue", "wed", "thu", "fri", "sat", "sun"};
    List<FavoriteTimetableDto> resultList = new ArrayList<>();

    for (String day : days) {
      String newBitHex = getTimeBitByDay(request, day);
      if (newBitHex == null || newBitHex.trim().isEmpty()) continue;

      Long newBitLong = Long.parseUnsignedLong(newBitHex, 16);
      Optional<FavoriteTimetable> existing = myTimetableRepository.findByMemberIdAndDay(memberId, day);

      if (existing.isPresent()) {
        FavoriteTimetable schedule = existing.get();
        Long updatedBit = schedule.getTimeBit() ^ newBitLong; // 간단한 XOR

        if (updatedBit == 0L) {
          myTimetableRepository.delete(schedule);
        } else {
          schedule.setTimeBit(updatedBit);
          myTimetableRepository.save(schedule);
          resultList.add(FavoriteTimetableDto.fromEntity(schedule));
        }
      } else {
        FavoriteTimetable newSchedule = FavoriteTimetable.of(member, day, newBitLong);
        myTimetableRepository.save(newSchedule);
        resultList.add(FavoriteTimetableDto.fromEntity(newSchedule));
      }
    }

    return resultList;
  }

  public List<FavoriteTimetableDto> getFavoriteTimetables(String memberId) {
    List<FavoriteTimetable> timetables = myTimetableRepository.findAllByMemberId(memberId);
    return timetables.stream()
        .map(FavoriteTimetableDto::fromEntity)
        .collect(Collectors.toList());
  }

  private String getTimeBitByDay(CreateFavoriteTimeRequest req, String day) {
    return switch (day) {
      case "mon" -> req.getTimeBitMon();
      case "tue" -> req.getTimeBitTue();
      case "wed" -> req.getTimeBitWed();
      case "thu" -> req.getTimeBitThu();
      case "fri" -> req.getTimeBitFri();
      case "sat" -> req.getTimeBitSat();
      case "sun" -> req.getTimeBitSun();
      default -> null;
    };
  }

  public CreateFavoriteTimeResponse getFavoriteTimetableResponse(String memberId) {
    List<FavoriteTimetableDto> dtos = getFavoriteTimetables(memberId);

    Map<String, String> dayToBitMap = dtos.stream()
        .collect(Collectors.toMap(
            FavoriteTimetableDto::getDay,
            dto -> String.format("%012X", dto.getTimeBit())
        ));

    return FavoriteTimetableDto.fromDto(dayToBitMap);
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
