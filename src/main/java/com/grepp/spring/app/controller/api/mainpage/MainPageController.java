package com.grepp.spring.app.controller.api.mainpage;

import com.grepp.spring.app.controller.api.mainpage.payload.response.ShowMainPageResponse;
import com.grepp.spring.app.model.mainpage.dto.UnifiedScheduleDto;
import com.grepp.spring.app.model.mainpage.service.MainPageService;
import com.grepp.spring.app.model.mainpage.service.MainPageService.UnifiedScheduleResult;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.infra.error.exceptions.mypage.AuthenticationRequiredException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.MyPageErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main-page")
public class MainPageController {

  private final MainPageService mainPageService;


  // 통합된 하나의 API
  @Operation(summary = "메인페이지", description = "회원의 그룹리스트, 일정 및 캘린더 조회")
  @GetMapping()
  public ResponseEntity<ApiResponse<ShowMainPageResponse>> getMainPage(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    // 기본 오늘 날짜
    LocalDate targetDate = (date != null) ? date : LocalDate.now();

    String memberId = extractCurrentMemberId();

    // 메인페이지 데이터 가져오기
    ShowMainPageResponse response = mainPageService.getMainPageData(memberId, targetDate);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "날짜 범위 지정 (내부 + 구글 공개) 일정 통합 조회 (메인페이지 확장)")
  @GetMapping("/calendar")
  public ApiResponse<Map<LocalDate,List<UnifiedScheduleDto>>> getSchedulesInRange(
      @RequestParam LocalDate startDate,
      @RequestParam LocalDate endDate
  ) {
    String memberId = extractCurrentMemberId();

    // start ~ end 날짜 동안의 통합 일정 조회
    UnifiedScheduleResult result = mainPageService.getUnifiedSchedules(memberId, startDate, endDate);

    Map<LocalDate, List<UnifiedScheduleDto>> groupedByDate = result.getSchedules().stream()
        .collect(Collectors.groupingBy(
            s -> s.getStartTime().toLocalDate(),
            TreeMap::new,
            Collectors.toList()
        ));

    Map<String, Object> response = new HashMap<>();
    response.put("googleCalendarFetchSuccess", result.isGoogleFetchSuccess());
    response.put("groupedSchedules", groupedByDate);

    return ApiResponse.success("월간 일정 조회 성공", groupedByDate);
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


