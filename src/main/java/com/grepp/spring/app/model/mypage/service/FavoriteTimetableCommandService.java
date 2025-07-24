package com.grepp.spring.app.model.mypage.service;

import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoriteTimeRequest;
import com.grepp.spring.app.controller.api.mypage.payload.response.CreateFavoriteTimeResponse;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.mypage.converter.FavoriteTimetableConverter;
import com.grepp.spring.app.model.mypage.dto.FavoriteTimetableDto;
import com.grepp.spring.app.model.mypage.entity.FavoriteTimetable;
import com.grepp.spring.app.model.mypage.repository.MyTimetableRepository;
import com.grepp.spring.infra.error.exceptions.mypage.InvalidFavoriteRequestException;
import com.grepp.spring.infra.error.exceptions.mypage.MemberNotFoundException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FavoriteTimetableCommandService {

  private final MyTimetableRepository myTimetableRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public CreateFavoriteTimeResponse createOrUpdateFavoriteTimetable(String memberId, CreateFavoriteTimeRequest request) {

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
    Map<String, String> dayToBitMap = FavoriteTimetableConverter.toDayBitMap(resultList);

    return FavoriteTimetableDto.fromDto(dayToBitMap);
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

}
