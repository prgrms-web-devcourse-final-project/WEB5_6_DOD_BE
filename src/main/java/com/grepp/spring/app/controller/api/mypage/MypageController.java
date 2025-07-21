package com.grepp.spring.app.controller.api.mypage;


import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoritePlaceRequest;
import com.grepp.spring.app.controller.api.mypage.payload.request.CreateFavoriteTimeRequest;
import com.grepp.spring.app.controller.api.mypage.payload.request.ModifyFavoritePlaceRequest;
import com.grepp.spring.app.controller.api.mypage.payload.response.CreateFavoritePlaceResponse;
import com.grepp.spring.app.controller.api.mypage.payload.response.CreateFavoriteTimeResponse;
import com.grepp.spring.app.controller.api.mypage.payload.response.ModifyFavoritePlaceResponse;
import com.grepp.spring.app.model.mypage.dto.FavoriteLocationDto;
import com.grepp.spring.app.model.mypage.dto.GoogleEventDto;
import com.grepp.spring.app.model.mypage.service.CalendarSyncService;
import com.grepp.spring.app.model.mypage.service.MypageService;
import com.grepp.spring.infra.error.exceptions.mypage.AuthenticationRequiredException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.MyPageErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    String memberId = extractCurrentMemberId();

    // 서비스에서 DTO 받아옴
    FavoriteLocationDto dto = mypageService.createFavoriteLocation(memberId, request);

    // 응답용 DTO 로 변환
    CreateFavoritePlaceResponse response = FavoriteLocationDto.fromDto(dto);

    // API 응답 감싸서 반환
    return ResponseEntity.ok(ApiResponse.success(response));

  }

  // 즐겨찾기 시간대 등록 및 수정
  @PostMapping("/favorite-timetable")
  @Operation(summary = "즐겨찾기 시간대 등록 및 수정", description = "회원 즐겨찾기 시간대 등록 및 수정")
  public ResponseEntity<ApiResponse<CreateFavoriteTimeResponse>> createOrUpdateFavoriteTime(
      @RequestBody @Valid CreateFavoriteTimeRequest request) {

    String memberId = extractCurrentMemberId();

    CreateFavoriteTimeResponse response = mypageService.createOrUpdateFavoriteTimetable(memberId,
        request);

    // API 응답 감싸서 반환
    return ResponseEntity.ok(ApiResponse.success(response));

  }


  @GetMapping("/favorite-location")
  @Operation(summary = "즐겨찾기 장소 조회", description = "회원 즐겨찾기 장소 조회")
  public ResponseEntity<ApiResponse<List<FavoriteLocationDto>>> getFavoriteLocations() {

    String memberId = extractCurrentMemberId();

    List<FavoriteLocationDto> response = mypageService.getFavoriteLocations(memberId);

    // API 응답 감싸서 반환
    return ResponseEntity.ok(ApiResponse.success(response));

  }

  @GetMapping("/favorite-timetable")
  @Operation(summary = "즐겨찾기 시간대 조회", description = "회원 즐겨찾기 시간대 조회")
  public ResponseEntity<ApiResponse<CreateFavoriteTimeResponse>> getFavoriteTimetables() {

    String memberId = extractCurrentMemberId();

    CreateFavoriteTimeResponse response = mypageService.getFavoriteTimetableResponse(memberId);

    return ResponseEntity.ok(ApiResponse.success(response));

  }


  // 즐겨찾기 장소 수정
  @Operation(summary = "즐겨찾기 장소 수정", description = "회원 즐겨찾기 장소 수정")
  @PatchMapping("/favorite-location")
  public ResponseEntity<ApiResponse<ModifyFavoritePlaceResponse>> modifyFavoritePlace(
      @RequestBody @Valid ModifyFavoritePlaceRequest request) {

    String memberId = extractCurrentMemberId();

    FavoriteLocationDto dto = mypageService.modifyFavoriteLocation(memberId, request);

    ModifyFavoritePlaceResponse response = FavoriteLocationDto.toModifyResponse(dto);

    return ResponseEntity.ok(ApiResponse.success(response));


  }


  @Operation(summary = "캘린더 동기화 새로고침")
  @PostMapping("/calendar/sync")
  public ApiResponse<List<GoogleEventDto>> syncCalendar() {

    String memberId = extractCurrentMemberId();

    List<GoogleEventDto> events = calendarSyncService.syncCalendar(memberId);

    return ApiResponse.success(events);
  }

  private String extractCurrentMemberId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // isAuthenticated -> 로그인, 비로그인 사용자 다 true
    // 로그인 한 사용자 토큰 : OAuth2AuthenticationToken
    // 로그인하지 않은 사용자도 token 을 줌. 토큰이 AnonymousAuthenticationToken 인지를 확인
    if (authentication == null ||
        authentication instanceof AnonymousAuthenticationToken) {
      throw new AuthenticationRequiredException(MyPageErrorCode.AUTHENTICATION_REQUIRED);
    }

    return authentication.getName();
  }

}
