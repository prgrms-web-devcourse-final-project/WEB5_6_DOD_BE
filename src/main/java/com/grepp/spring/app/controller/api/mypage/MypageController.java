package com.grepp.spring.app.controller.api.mypage;


import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoritePlaceRequest;
import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoriteTimeRequest;
import com.grepp.spring.app.controller.api.mypage.payload.request.ModifyFavoritePlaceRequest;
import com.grepp.spring.app.controller.api.mypage.payload.request.SetCalendarSyncRequest;
import com.grepp.spring.app.controller.api.mypage.payload.response.CreateFavoritePlaceResponse;
import com.grepp.spring.app.controller.api.mypage.payload.response.CreateFavoriteTimeResponse;
import com.grepp.spring.app.controller.api.mypage.payload.response.ModifyFavoritePlaceResponse;
import com.grepp.spring.app.controller.api.mypage.payload.response.ModifyProfileResponse;
import com.grepp.spring.app.controller.api.mypage.payload.response.SetCalendarSyncResponse;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.mypage.dto.FavoriteLocationDto;
import com.grepp.spring.app.model.mypage.dto.FavoriteTimetableDto;
import com.grepp.spring.app.model.mypage.dto.GoogleEventDto;
import com.grepp.spring.app.model.mypage.service.CalendarSyncService;
import com.grepp.spring.app.model.mypage.service.MypageService;
import com.grepp.spring.infra.error.exceptions.AuthApiException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MypageController {

  private final MypageService mypageService;
  private final CalendarSyncService calendarSyncService;

  // 즐겨찾기 장소 등록
  @PostMapping("/favorite-location")
  @Operation(summary = "즐겨찾기 장소 등록", description = "회원 즐겨찾기 장소 등록")
  public ResponseEntity<ApiResponse<CreateFavoritePlaceResponse>> createFavoriteLocation(
      @RequestBody @Valid CreateFavoritePlaceRequest request) {

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null) {
        throw new IllegalStateException("로그인된 사용자 정보를 확인할 수 없습니다.");
      }

      String memberId = authentication.getName();

      // 서비스에서 DTO 받아옴
      FavoriteLocationDto dto = mypageService.createFavoriteLocation(memberId, request);

      // 응답용 DTO 로 변환
      CreateFavoritePlaceResponse response = FavoriteLocationDto.fromDto(dto);

      // API 응답 감싸서 반환
      return ResponseEntity.ok(ApiResponse.success(response));

    } catch (IllegalStateException e) {
      return ResponseEntity.status(409)
          .body(ApiResponse.error(ResponseCode.CONFLICT_REGISTER, "이미 즐겨찾기 장소를 등록했습니다."));

    } catch (Exception e) {
      if (e instanceof AuthenticationException) {
        return ResponseEntity.status(401)
            .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "권한이 없습니다."));
      }
      return ResponseEntity.status(400)
          .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
    }
  }

  // 즐겨찾기 시간대 등록 및 수정
  @PostMapping("/favorite-timetable")
  @Operation(summary = "즐겨찾기 시간대 등록 및 수정", description = "회원 즐겨찾기 시간대 등록 및 수정")
  public ResponseEntity<ApiResponse<CreateFavoriteTimeResponse>> createOrUpdateFavoriteTime(
      @RequestBody @Valid CreateFavoriteTimeRequest request) {

    try {

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null) {
        throw new IllegalStateException("로그인된 사용자 정보를 확인할 수 없습니다.");
      }

      String memberId = authentication.getName();

      List<FavoriteTimetableDto> dtos = mypageService.createOrUpdateFavoriteTimetable(memberId,
          request);

      // 요일 → 비트값 맵 생성
      Map<String, String> dayToBitMap = dtos.stream()
          .collect(Collectors.toMap(
              FavoriteTimetableDto::getDay,
              dto -> String.format("%012X", dto.getTimeBit())
          ));

      CreateFavoriteTimeResponse response = FavoriteTimetableDto.fromDto(dayToBitMap);

      // API 응답 감싸서 반환
      return ResponseEntity.ok(ApiResponse.success(response));

    } catch (IllegalStateException e) {
      return ResponseEntity.status(409)
          .body(
              ApiResponse.error(ResponseCode.CONFLICT_REGISTER, "해당 시간대는 기존에 등록된 즐겨찾기 시간대와 겹칩니다."));
    } catch (Exception e) {
      if (e instanceof AuthApiException) {
        return ResponseEntity.status(401)
            .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "권한이 없습니다."));
      }
      return ResponseEntity.status(400)
          .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
    }
  }

  @GetMapping("/favorite-location")
  @Operation(summary = "즐겨찾기 장소 조회", description = "회원 즐겨찾기 장소 조회")
  public ResponseEntity<ApiResponse<List<FavoriteLocationDto>>> getFavoriteLocations() {
    try {

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null) {
        throw new IllegalStateException("로그인된 사용자 정보를 확인할 수 없습니다.");
      }

      String memberId = authentication.getName();
      List<FavoriteLocationDto> result = mypageService.getFavoriteLocations(memberId);

      // API 응답 감싸서 반환
      return ResponseEntity.ok(ApiResponse.success(result));


    } catch (Exception e) {
      if (e instanceof AuthApiException) {
        return ResponseEntity.status(401)
            .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "권한이 없습니다."));
      }
      return ResponseEntity.status(400)
          .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
    }
  }

  @GetMapping("/favorite-timetable")
  @Operation(summary = "즐겨찾기 시간대 조회", description = "회원 즐겨찾기 시간대 조회")
  public ResponseEntity<ApiResponse<CreateFavoriteTimeResponse>> getFavoriteTimetables() {
    try {

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null) {
        throw new IllegalStateException("로그인된 사용자 정보를 확인할 수 없습니다.");
      }

      String memberId = authentication.getName();
      CreateFavoriteTimeResponse response = mypageService.getFavoriteTimetableResponse(memberId);

      return ResponseEntity.ok(ApiResponse.success(response));

    } catch (Exception e) {
      if (e instanceof AuthApiException) {
        return ResponseEntity.status(401)
            .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "권한이 없습니다."));
      }
      return ResponseEntity.status(400)
          .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
    }
  }


  // 즐겨찾기 장소 수정
  @Operation(summary = "즐겨찾기 장소 수정", description = "회원 즐겨찾기 장소 수정")
  @PatchMapping("/favorite-location")
  public ResponseEntity<ApiResponse<ModifyFavoritePlaceResponse>> modifyFavoritePlace(
      @RequestBody @Valid ModifyFavoritePlaceRequest request) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null) {
        throw new IllegalStateException("로그인된 사용자 정보를 확인할 수 없습니다.");
      }

      String memberId = authentication.getName();

      FavoriteLocationDto dto = mypageService.modifyFavoriteLocation(memberId, request);

      ModifyFavoritePlaceResponse response = FavoriteLocationDto.toModifyResponse(dto);

      return ResponseEntity.ok(ApiResponse.success(response));


    } catch (Exception e) {
      if (e instanceof AuthenticationException) {
        return ResponseEntity.status(401)
            .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "인증(로그인)이 되어있지 않습니다."));
      }
      return ResponseEntity.status(400)
          .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "필수값이 누락되었습니다."));
    }
  }

  @Operation(summary = "캘린더 동기화 새로고침")
  @PostMapping("/calendar/sync")
  public ApiResponse<List<GoogleEventDto>> syncCalendar() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new AuthenticationCredentialsNotFoundException("로그인 필요");
    }

    String memberId = authentication.getName(); //

    return calendarSyncService.syncCalendar(memberId);
  }
}