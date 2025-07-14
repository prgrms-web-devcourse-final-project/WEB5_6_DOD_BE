package com.grepp.spring.app.controller.api.mainpage;


import com.grepp.spring.app.controller.api.mypage.payload.response.CalendarSyncStatusResponse;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.mainpage.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/calendar")
public class CalendarController {

  private final CalendarService calendarService;

//  @GetMapping("/sync-status")
//  public ResponseEntity<CalendarSyncStatusResponse> getCalendar(
//      @AuthenticationPrincipal Principal principal
//  ) {
//
//    if (principal == null) {
//      throw new AuthenticationCredentialsNotFoundException("로그인 필요");
//    }
//
//    CalendarSyncStatusResponse response = calendarService.getCalendarSyncStatus(principal.getUsername());
//    return ResponseEntity.ok(response);
//  }

}
