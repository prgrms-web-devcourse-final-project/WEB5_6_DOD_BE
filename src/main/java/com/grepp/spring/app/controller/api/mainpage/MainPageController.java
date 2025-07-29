package com.grepp.spring.app.controller.api.mainpage;

import com.grepp.spring.app.controller.api.mainpage.payload.response.ShowMainPageResponse;
import com.grepp.spring.app.controller.api.mainpage.payload.response.UpdateActivationResponse;
import com.grepp.spring.app.model.mainpage.dto.UnifiedScheduleDto;
import com.grepp.spring.app.model.mainpage.service.MainPageScheduleMemberService;
import com.grepp.spring.app.model.mainpage.service.MainPageService;
import com.grepp.spring.app.model.mainpage.service.MainPageService.UnifiedScheduleResult;
import com.grepp.spring.infra.auth.CurrentUser;
import com.grepp.spring.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main-page")
public class MainPageController {

  private final MainPageService mainPageService;

  private final MainPageScheduleMemberService mainPageScheduleMemberService;


  // 통합된 하나의 API
  @Operation(summary = "메인페이지", description = "회원의 그룹리스트, 일정 및 캘린더 조회")
  @GetMapping()
  public ResponseEntity<ApiResponse<ShowMainPageResponse>> getMainPage(
      @CurrentUser String userId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    // 기본 오늘 날짜
    LocalDate targetDate = (date != null) ? date : LocalDate.now();

    // 메인페이지 데이터 가져오기
    ShowMainPageResponse response = mainPageService.getMainPageData(userId, targetDate);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "날짜 범위 지정 (내부 + 구글 공개) 일정 통합 조회 (메인페이지 확장)")
  @GetMapping("/calendar")
  public  ResponseEntity<ApiResponse<Map<String, Object>>> getSchedulesInRange(
      @CurrentUser String userId,
      @RequestParam LocalDate startDate,
      @RequestParam LocalDate endDate
  ) {
    // start ~ end 날짜 동안의 통합 일정 조회
    UnifiedScheduleResult result = mainPageService.getUnifiedSchedules(userId, startDate, endDate);

    Map<LocalDate, List<UnifiedScheduleDto>> groupedByDate = result.getSchedules().stream()
        .collect(Collectors.groupingBy(
            s -> s.getStartTime().toLocalDate(),
            TreeMap::new,
            Collectors.toList()
        ));

    Map<String, Object> response = new HashMap<>();
    response.put("googleCalendarFetchSuccess", result.isGoogleFetchSuccess());
    response.put("groupedSchedules", groupedByDate);
    response.put("groupDetails", result.getGroups()); // 그룹 정보

    return  ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "내부 일정 목록만 조회하는 기능")
  @GetMapping("/schedules")
  public ApiResponse<List<UnifiedScheduleDto>> getScheduleLists(
      @CurrentUser String userId,
      @RequestParam LocalDate startDate,
      @RequestParam LocalDate endDate
  ) {

    List<UnifiedScheduleDto> schedules = mainPageService.getInternalSchedules(userId, startDate, endDate);

    return ApiResponse.success(schedules);

  }

  @Operation(summary = "일정 비활성화 기능")
  @PatchMapping("/schedule-members/{scheduleMemberId}/activation")
  public ApiResponse<UpdateActivationResponse> activateScheduleMember(
      @PathVariable Long scheduleMemberId
  ){
    UpdateActivationResponse response =
        mainPageScheduleMemberService.updateActivation(scheduleMemberId);

    return ApiResponse.success(response);
  }

}


