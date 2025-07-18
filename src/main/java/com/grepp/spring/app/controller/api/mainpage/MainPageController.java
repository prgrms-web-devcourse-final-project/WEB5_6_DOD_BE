package com.grepp.spring.app.controller.api.mainpage;

import com.grepp.spring.app.controller.api.mainpage.payload.response.ShowMainPageResponse;
import com.grepp.spring.app.model.mainpage.dto.UnifiedScheduleDto;
import com.grepp.spring.app.model.mainpage.service.CalendarService;
import com.grepp.spring.app.model.mainpage.service.MainPageService;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.entity.SocialAuthToken;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.member.repository.SocialAuthTokenRepository;
import com.grepp.spring.app.model.mypage.service.SocialAuthTokenService;
import com.grepp.spring.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
  private final CalendarService calendarService;

  private final SocialAuthTokenRepository socialAuthTokenRepository;
  private final MemberRepository memberRepository;


  // 통합된 하나의 API
  @Operation(summary = "메인페이지", description = "회원의 그룹리스트, 일정 및 캘린더 조회")
  @GetMapping()
  public ResponseEntity<ApiResponse<ShowMainPageResponse>> getMainPage(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    // 기본 오늘 날짜
    LocalDate targetDate = (date != null) ? date : LocalDate.now();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new IllegalStateException("로그인된 사용자 정보를 확인할 수 없습니다.");
    }
    String memberId = authentication.getName();

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

    // 메인페이지 데이터 가져오기
    ShowMainPageResponse response = mainPageService.getMainPageData(memberId, targetDate);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "날짜 범위 지정 일정 조회 (메인페이지 확장)")
  @GetMapping("/calendar")
  public ApiResponse<Map<LocalDate,List<UnifiedScheduleDto>>> getMonthlySchedules(
      @RequestParam LocalDate startDate,
      @RequestParam LocalDate endDate
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new IllegalStateException("로그인된 사용자 정보를 확인할 수 없습니다.");
    }
    String memberId = authentication.getName();

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));


    Optional<SocialAuthToken> tokenOpt = socialAuthTokenRepository.findByMember(member);
    if (tokenOpt.isEmpty()) {
      throw new IllegalStateException("구글 캘린더 연동이 필요합니다.");
    }

    // 그냥 해당 월의 1일만 CalendarService에 넘기면 알아서 월간 범위 조회
    LocalDate anyDateInMonth = LocalDate.of(
        startDate.getYear(),
        startDate.getMonthValue(),
        1
    );

    Map<LocalDate, List<UnifiedScheduleDto>> monthlySchedules =
        calendarService.getMonthlySchedules(memberId, startDate, endDate);

    return ApiResponse.success("월간 일정 조회 성공",monthlySchedules);
  }

}


