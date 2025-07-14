package com.grepp.spring.app.controller.api.event;

import com.grepp.spring.app.controller.api.event.payload.request.CreateEventRequest;
import com.grepp.spring.app.controller.api.event.payload.request.CreateScheduleResultRequest;
import com.grepp.spring.app.controller.api.event.payload.request.MyTimeScheduleRequest;
import com.grepp.spring.app.controller.api.event.payload.response.*;
import com.grepp.spring.app.model.event.dto.AllTimeScheduleDto;
import com.grepp.spring.app.model.event.dto.MyTimeScheduleDto;
import com.grepp.spring.app.model.event.service.EventService;
import com.grepp.spring.infra.error.exceptions.AuthApiException;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/events", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    @PostMapping
    @Operation(summary = "이벤트 생성", description = "그룹 이벤트 또는 일회성 이벤트를 생성합니다.")
    public ResponseEntity<ApiResponse<Void>> createEvent(@RequestBody @Valid CreateEventRequest request) {
        try {
            String currentMemberId = extractCurrentMemberId();

            eventService.createEvent(request, currentMemberId);

            return ResponseEntity.status(200)
                .body(ApiResponse.success("이벤트가 성공적으로 생성되었습니다."));

        } catch (AuthApiException e) {
            log.warn("이벤트 생성 권한 오류: {}", e.getMessage());
            return ResponseEntity.status(401)
                .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, e.getMessage()));

        } catch (NotFoundException e) {
            log.warn("이벤트 생성 시 리소스 없음: {}", e.getMessage());
            return ResponseEntity.status(404)
                .body(ApiResponse.error(ResponseCode.NOT_FOUND, e.getMessage()));

        } catch (Exception e) {
            log.error("이벤트 생성 중 예상치 못한 오류", e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."));
        }
    }

    // 이벤트 일정 참여
    @Operation(summary = "이벤트 참여")
    @PostMapping("/{eventId}")
    public ResponseEntity<ApiResponse<Void>> joinEvent(@PathVariable Long eventId) {

        try {
            String currentMemberId = extractCurrentMemberId();

            eventService.joinEvent(eventId, currentMemberId);

            return ResponseEntity.ok(ApiResponse.success("이벤트에 성공적으로 참여했습니다."));

        } catch (NotFoundException e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(ResponseCode.NOT_FOUND, e.getMessage()));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(400)
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."));
        }
    }

    // 개인의 가능한 시간대 생성/수정
    @PostMapping("/{eventId}/my-time")
    @Operation(summary = "개인의 가능한 시간대 생성/수정")
    public ResponseEntity<ApiResponse<Void>> createOrUpdateMyTime(
        @PathVariable Long eventId,
        @RequestBody @Valid MyTimeScheduleRequest request) {

        try {
            String currentMemberId = extractCurrentMemberId();

            eventService.createOrUpdateMyTime(request, eventId, currentMemberId);

            return ResponseEntity.ok(ApiResponse.success("개인 일정이 성공적으로 생성/수정되었습니다."));

        } catch (NotFoundException e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(ResponseCode.NOT_FOUND, e.getMessage()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400)
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."));
        }
    }

    // 참여자 전원의 가능한 시간대 조회
    @Operation(summary = "참여자 전원의 가능한 시간대 조회")
    @GetMapping("/{eventId}/all-time")
    public ResponseEntity<ApiResponse<AllTimeScheduleResponse>> getAllTimeSchedules(@PathVariable Long eventId) {

        try {
            String currentMemberId = extractCurrentMemberId();

            AllTimeScheduleResponse response = eventService.getAllTimeSchedules(eventId, currentMemberId);

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (NotFoundException e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error(ResponseCode.NOT_FOUND, e.getMessage()));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(403)
                .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."));
        }
    }

    // 개인의 가능한 시간대 확정
    @Operation(summary = "개인의 가능한 시간대 확정")
    @PostMapping("/{eventId}/complete")
    public ResponseEntity<ApiResponse<CompleteMyTimeResponse>> completeMyTime(@PathVariable Long eventId) {

        try {
            if (
                eventId != 20000 && eventId != 20001 && eventId != 20002 &&
                    eventId != 20003 && eventId != 20004 && eventId != 20005
            ) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다."));
            }
            return ResponseEntity.ok(ApiResponse.success("개인 일정이 성공적으로 확정되었습니다."));
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                return ResponseEntity.status(401).body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "권한이 없습니다."));
            }
            return ResponseEntity.status(400)
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
        }
    }

    // 이벤트 조율 결과 생성
    @Operation(summary = "이벤트 조율 결과 생성")
    @PostMapping("/events/{eventId}/all-time")
    public ResponseEntity<ApiResponse<CreateScheduleResultResponse>> createScheduleResult(
        @PathVariable Long eventId,
        @RequestBody @Valid CreateScheduleResultRequest request) {

        try {
            if (
                eventId != 20000 && eventId != 20001 && eventId != 20002 &&
                    eventId != 20003 && eventId != 20004 && eventId != 20005
            ) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다."));
            }
            return ResponseEntity.ok(ApiResponse.success("일정 조율 결과가 성공적으로 생성되었습니다."));
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                return ResponseEntity.status(401).body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "권한이 없습니다."));
            }
            return ResponseEntity.status(400)
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
        }
    }

    // 이벤트 조율 결과 조회
    @Operation(summary = "이벤트 조율 결과 조회")
    @GetMapping("/{eventId}/all-time/result")
    public ResponseEntity<ApiResponse<ScheduleResultResponse>> getScheduleResult(@PathVariable Long eventId) {

        try {
            if (
                eventId != 20000 && eventId != 20001 && eventId != 20002 &&
                    eventId != 20003 && eventId != 20004 && eventId != 20005
            ) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다."));
            }

            ScheduleResultResponse response = new ScheduleResultResponse();
            response.setEventTitle("카츠오모이 가는날");
            response.setTotalParticipants(6);

            // 시간대별 상세 정보
            List<ScheduleResultResponse.TimeSlotDetail> timeSlotDetails = new ArrayList<>();

            // 첫 번째 시간대 (7월 금요일)
            ScheduleResultResponse.TimeSlotDetail slot1 = new ScheduleResultResponse.TimeSlotDetail();
            slot1.setStartTime(LocalDateTime.of(2025, 7, 11, 18, 0));
            slot1.setEndTime(LocalDateTime.of(2025, 7, 11, 22, 0));
            slot1.setParticipantCount(6);
            slot1.setIsSelected(false);
            slot1.setTimeSlotId("slot_1");

            // 첫 번째 시간대 참여자들
            List<ScheduleResultResponse.Participant> participants1 = new ArrayList<>();
            String[] names = {"박은서", "한예주", "박은규", "박상욱", "황수지", "배수지"};
            for (String name : names) {
                ScheduleResultResponse.Participant participant = new ScheduleResultResponse.Participant();
                participant.setMemberId("google_" + name.hashCode());
                participant.setMemberName(name);
                participants1.add(participant);
            }
            slot1.setParticipants(participants1);

            // 두 번째 시간대 (7월 토요일)
            ScheduleResultResponse.TimeSlotDetail slot2 = new ScheduleResultResponse.TimeSlotDetail();
            slot2.setStartTime(LocalDateTime.of(2025, 7, 12, 18, 0));
            slot2.setEndTime(LocalDateTime.of(2025, 7, 12, 22, 0));
            slot2.setParticipantCount(6);
            slot2.setIsSelected(false);
            slot2.setTimeSlotId("slot_2");
            slot2.setParticipants(new ArrayList<>(participants1)); // 동일한 참여자들

            // 세 번째 시간대 (7월 일요일)
            ScheduleResultResponse.TimeSlotDetail slot3 = new ScheduleResultResponse.TimeSlotDetail();
            slot3.setStartTime(LocalDateTime.of(2025, 7, 13, 18, 0));
            slot3.setEndTime(LocalDateTime.of(2025, 7, 13, 22, 0));
            slot3.setParticipantCount(6);
            slot3.setIsSelected(false);
            slot3.setTimeSlotId("slot_3");
            slot3.setParticipants(new ArrayList<>(participants1)); // 동일한 참여자들

            timeSlotDetails.add(slot1);
            timeSlotDetails.add(slot2);
            timeSlotDetails.add(slot3);

            ScheduleResultResponse.Recommendation recommendationSummary = new ScheduleResultResponse.Recommendation();
            recommendationSummary.setLongestMeetingTime(slot1);
            recommendationSummary.setEarliestMeetingTime(slot1);

            response.setRecommendation(recommendationSummary);

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                return ResponseEntity.status(401).body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "권한이 없습니다."));
            }
            return ResponseEntity.status(400)
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
        }

    }

    // 이벤트 삭제
    @Operation(summary = "이벤트 삭제")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiResponse<DeleteEventResponse>> deleteEvent(@PathVariable Long eventId) {

        try {
            if (
                eventId != 20000 && eventId != 20001 && eventId != 20002 &&
                    eventId != 20003 && eventId != 20004 && eventId != 20005
            ) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다."));
            }
            return ResponseEntity.ok(ApiResponse.success("이벤트가 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                return ResponseEntity.status(401).body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "권한이 없습니다."));
            }
            return ResponseEntity.status(400)
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
        }
    }

    private String extractCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
            !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        return authentication.getName();
    }

}
