package com.grepp.spring.app.controller.api.event;

import com.grepp.spring.app.controller.api.event.payload.request.CreateEventRequest;
import com.grepp.spring.app.controller.api.event.payload.request.MyTimeScheduleRequest;
import com.grepp.spring.app.controller.api.event.payload.response.AllTimeScheduleResponse;
import com.grepp.spring.app.controller.api.event.payload.response.CreateEventResponse;
import com.grepp.spring.app.controller.api.event.payload.response.ScheduleResultResponse;
import com.grepp.spring.app.controller.api.event.payload.response.ShowEventResponse;
import com.grepp.spring.app.model.event.service.EventService;
import com.grepp.spring.infra.error.exceptions.event.EventAuthenticationException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.EventErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/v1/events", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    @PostMapping
    @Operation(summary = "이벤트 생성", description = "그룹 이벤트 또는 일회성 이벤트를 생성합니다.")
    public ResponseEntity<ApiResponse<CreateEventResponse>> createEvent(@RequestBody @Valid CreateEventRequest request) {

        String currentMemberId = extractCurrentMemberId();

        CreateEventResponse response = eventService.createEvent(request, currentMemberId);

        return ResponseEntity.status(200)
            .body(ApiResponse.success("이벤트가 성공적으로 생성되었습니다.", response));

    }

    // 이벤트 조회
    @Operation(summary = "이벤트 조회")
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<ShowEventResponse>> getEvent(@PathVariable Long eventId) {

        String currentMemberId = extractCurrentMemberId();

        ShowEventResponse response = eventService.getEvent(eventId, currentMemberId);

        return ResponseEntity.ok(ApiResponse.success("이벤트 조회가 성공적으로 완료되었습니다.", response));

    }

    // 이벤트 일정 참여
    @Operation(summary = "이벤트 참여")
    @PostMapping("/{eventId}/join/{groupId}")
    public ResponseEntity<ApiResponse<Void>> joinEvent(
        @PathVariable Long eventId,
        @PathVariable Long groupId) {

        String currentMemberId = extractCurrentMemberId();

        eventService.joinEvent(eventId, groupId, currentMemberId);

        return ResponseEntity.ok(ApiResponse.success("이벤트에 성공적으로 참여했습니다."));

    }

    // 개인의 가능한 시간대 생성/수정
    @PostMapping("/{eventId}/my-time")
    @Operation(summary = "개인의 가능한 시간대 생성/수정")
    public ResponseEntity<ApiResponse<Void>> createOrUpdateMyTime(
        @PathVariable Long eventId,
        @RequestBody @Valid MyTimeScheduleRequest request) {

        String currentMemberId = extractCurrentMemberId();

        eventService.createOrUpdateMyTime(request, eventId, currentMemberId);

        return ResponseEntity.ok(ApiResponse.success("개인 일정이 성공적으로 생성/수정되었습니다."));

    }

    // 참여자 전원의 가능한 시간대 조회
    @Operation(summary = "참여자 전원의 가능한 시간대 조회")
    @GetMapping("/{eventId}/all-time")
    public ResponseEntity<ApiResponse<AllTimeScheduleResponse>> getAllTimeSchedules(@PathVariable Long eventId) {

        String currentMemberId = extractCurrentMemberId();

        AllTimeScheduleResponse response = eventService.getAllTimeSchedules(eventId, currentMemberId);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

    // 개인의 가능한 시간대 확정
    @Operation(summary = "개인의 가능한 시간대 확정")
    @PostMapping("/{eventId}/complete")
    public ResponseEntity<ApiResponse<Void>> completeMyTime(@PathVariable Long eventId) {

        String currentMemberId = extractCurrentMemberId();

        eventService.completeMyTime(eventId, currentMemberId);

        return ResponseEntity.ok(ApiResponse.success("개인 일정이 성공적으로 확정되었습니다."));

    }

    // 이벤트 조율 결과 생성
    @Operation(summary = "이벤트 조율 결과 생성")
    @PostMapping("/{eventId}/schedule-result")
    public ResponseEntity<ApiResponse<Void>> createScheduleResult(
        @PathVariable Long eventId) {

        String currentMemberId = extractCurrentMemberId();

        eventService.createScheduleResult(eventId, currentMemberId);

        return ResponseEntity.ok(ApiResponse.success("일정 조율 결과가 성공적으로 생성되었습니다."));

    }

    // 이벤트 조율 결과 조회
    @Operation(summary = "이벤트 조율 결과 조회")
    @GetMapping("/{eventId}/all-time/result")
    public ResponseEntity<ApiResponse<ScheduleResultResponse>> getScheduleResult(@PathVariable Long eventId) {

        String currentMemberId = extractCurrentMemberId();

        ScheduleResultResponse response = eventService.getScheduleResult(eventId, currentMemberId);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

    private String extractCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
            !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {

            throw new EventAuthenticationException(EventErrorCode.AUTHENTICATION_REQUIRED);
        }

        return authentication.getName();
    }

}
