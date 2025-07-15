package com.grepp.spring.app.controller.api.mainpage;

import com.grepp.spring.app.controller.api.mainpage.payload.response.ShowMainPageResponse;
import com.grepp.spring.app.model.mainpage.service.MainPageService;
import com.grepp.spring.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MainPageController {

  private final MainPageService mainPageService;

  // 통합된 하나의 API
  @Operation(summary = "메인페이지", description = "회원의 그룹리스트, 일정 및 캘린더 조회")
  @GetMapping("/main-page")
  public ResponseEntity<ApiResponse<ShowMainPageResponse>> getMainPage(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      Authentication authentication
  ) {
    String memberId = authentication.getName();
    // 기본 오늘 날짜
    LocalDate targetDate = (date != null) ? date : LocalDate.now();

    // 메인페이지 데이터 가져오기
    ShowMainPageResponse response = mainPageService.getMainPageData(memberId, targetDate);

    return ResponseEntity.ok(ApiResponse.success(response));
  }
}


