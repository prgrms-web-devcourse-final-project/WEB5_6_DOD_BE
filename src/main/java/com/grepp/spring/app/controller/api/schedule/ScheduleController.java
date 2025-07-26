package com.grepp.spring.app.controller.api.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.grepp.spring.app.controller.api.schedule.payload.request.AddWorkspaceRequest;
import com.grepp.spring.app.controller.api.schedule.payload.request.CreateDepartLocationRequest;
import com.grepp.spring.app.controller.api.schedule.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.controller.api.schedule.payload.request.ModifySchedulesRequest;
import com.grepp.spring.app.controller.api.schedule.payload.request.VoteMiddleLocationsRequest;
import com.grepp.spring.app.controller.api.schedule.payload.request.WriteSuggestedLocationRequest;
import com.grepp.spring.app.controller.api.schedule.payload.response.CreateDepartLocationResponse;
import com.grepp.spring.app.controller.api.schedule.payload.response.CreateOnlineMeetingRoomResponse;
import com.grepp.spring.app.controller.api.schedule.payload.response.CreateSchedulesResponse;
import com.grepp.spring.app.controller.api.schedule.payload.response.CreateWorkspaceResponse;
import com.grepp.spring.app.controller.api.schedule.payload.response.DeleteSchedulesResponse;
import com.grepp.spring.app.controller.api.schedule.payload.response.DeleteWorkSpaceResponse;
import com.grepp.spring.app.controller.api.schedule.payload.response.ShowScheduleResponse;
import com.grepp.spring.app.controller.api.schedule.payload.response.ShowSuggestedLocationsResponse;
import com.grepp.spring.app.controller.api.schedule.payload.response.ShowVoteMembersResponse;
import com.grepp.spring.app.controller.api.schedule.payload.response.VoteMiddleLocationsResponse;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import com.grepp.spring.app.model.schedule.service.ScheduleCommandService;
import com.grepp.spring.app.model.schedule.service.ScheduleQueryService;
import com.grepp.spring.infra.auth.CurrentUser;
import com.grepp.spring.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleQueryService scheduleQueryService;
    private final ScheduleCommandService scheduleCommandService;

    // 일정을 검증하기 위한 공통 메서드
    private Schedule validSchedule(Long scheduleId) {
        return scheduleQueryService.findScheduleById(scheduleId);
    }

    // 일정 조회
    @Operation(summary = "일정 조회", description = "일정을 조회합니다.")
    @GetMapping("/show/{scheduleId}")
    public ResponseEntity<ApiResponse<ShowScheduleResponse>> showSchedules(
        @PathVariable Long scheduleId) {

        Schedule schedule = validSchedule(scheduleId);
        ShowScheduleResponse response = scheduleQueryService.showSchedule(schedule);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // 일정 등록
    @Operation(summary = "일정 등록", description = "일정 등록을 진행합니다.")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateSchedulesResponse>> createSchedules(
        @RequestBody CreateSchedulesRequest request, @CurrentUser String userId) {

        Event event = scheduleQueryService.findEventById(request.getEventId());

        CreateSchedulesResponse response = scheduleCommandService.createSchedule(request, event, userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 일정 수정
    @Operation(summary = "일정 수정", description = "일정 수정을 진행합니다.")
    @PatchMapping("/modify/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> modifySchedule(
        @PathVariable Long scheduleId, @RequestBody ModifySchedulesRequest request,
        @CurrentUser String userId) {

        Schedule schedule = validSchedule(scheduleId);
        scheduleCommandService.modifySchedule(request, schedule.getId(), userId);

        return ResponseEntity.ok(ApiResponse.success("일정이 수정되었습니다."));
    }

    // 일정 삭제
    @Operation(summary = "일정 삭제", description = "일정을 삭제합니다.")
    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<ApiResponse<DeleteSchedulesResponse>> deleteSchedules(
        @PathVariable Long scheduleId, @CurrentUser String userId) {

        Schedule schedule = validSchedule(scheduleId);
        scheduleCommandService.deleteSchedule(schedule.getId(), userId);

        return ResponseEntity.ok(ApiResponse.success("일정을 삭제했습니다."));
    }


    // 출발장소 등록
    @Operation(summary = "출발장소 등록", description = "출발장소 등록을 진행합니다.")
    @PostMapping("create-depart-location/{scheduleId}")
    public ResponseEntity<ApiResponse<CreateDepartLocationResponse>> createDepartLocation(
        @RequestParam Long scheduleId, @RequestBody CreateDepartLocationRequest request,
        @CurrentUser String userId)
        throws JsonProcessingException {

        Schedule schedule = validSchedule(scheduleId);

        scheduleCommandService.createDepartLocation(schedule.getId(), request, userId);

        return ResponseEntity.ok(ApiResponse.success("출발장소가 등록되었습니다."));
    }

    // 중간장소 후보 조회
    @Operation(summary = "중간장소 후보 조회 & 중간장소 투표결과 확인", description = "중간장소 후보를 조회 & 중간장소 투표결과 확인 합니다.")
    @GetMapping("/show-suggested-locations/{scheduleId}")
    public ResponseEntity<ApiResponse<ShowSuggestedLocationsResponse>> showSuggestedLocations(
        @PathVariable Long scheduleId) {

        Schedule schedule = validSchedule(scheduleId);
        ShowSuggestedLocationsResponse response = scheduleQueryService.showSuggestedLocation(
            schedule.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 투표 전 중간장소 직접 입력받기
    @Operation(summary = "투표 전 중간장소 직접 입력받기", description = "투표 전 중간장소를 직접 입력받습니다.")
    @PostMapping("/write-suggested-location/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> writeSuggestedLocation(@PathVariable Long scheduleId,
        @RequestBody WriteSuggestedLocationRequest request, @CurrentUser String userId) {

        Schedule schedule = validSchedule(scheduleId);
        scheduleCommandService.WriteSuggestedLocation(schedule, request, userId);

        return ResponseEntity.ok(ApiResponse.success("중간장소를 등록했습니다."));
    }

    // 중간장소 투표하기
    @Operation(summary = "중간장소 투표하기", description = "중간장소를 투표합니다.")
    @PostMapping("/suggested-locations/vote/{scheduleMemberId}")
    public ResponseEntity<ApiResponse<VoteMiddleLocationsResponse>> voteMiddleLocation(
        @PathVariable Long scheduleMemberId, @RequestBody VoteMiddleLocationsRequest request) {

        Schedule schedule = validSchedule(request.getScheduleId());
        ScheduleMember scheduleMember = scheduleQueryService.findScheduleMemberById(
            scheduleMemberId);
        Location location = scheduleQueryService.findLocationById(request.getLocationId());

        scheduleCommandService.voteMiddleLocation(schedule, scheduleMember, location);

        return ResponseEntity.ok(ApiResponse.success("성공적으로 투표를 진행했습니다."));
    }

    // 스케줄에 대해 투표한 인원의 아이디 확인
    @Operation(summary = "투표한 인원의 아이디를 확인합니다.", description = "투표한 인원의 아이디를 확인합니다.")
    @GetMapping("/show-vote-members/{scheduleId}")
    public ResponseEntity<ApiResponse<ShowVoteMembersResponse>> showVoteMembers(
        @PathVariable Long scheduleId) {

        Schedule schedule = validSchedule(scheduleId);

        ShowVoteMembersResponse response = scheduleQueryService.findVoteMembers(schedule.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 온라인 회의장 링크 개설(줌)
    @Operation(summary = "온라인 회의장 링크 개설(줌)", description = "온라인 회의장을 개설합니다.")
    @PostMapping("/create-online-meeting/{scheduleId}")
    public ResponseEntity<ApiResponse<CreateOnlineMeetingRoomResponse>> CreateOnlineMeetingRoom(
        @PathVariable Long scheduleId) {

        CreateOnlineMeetingRoomResponse response = scheduleCommandService.createOnlineMeeting(
            scheduleId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 공통 워크스페이스 등록
    @Operation(summary = "워크스페이스 등록", description = "워크스페이스 등록을 진행합니다.")
    @PostMapping("/add-workspace/{scheduleId}")
    public ResponseEntity<ApiResponse<CreateWorkspaceResponse>> createWorkspace(
        @PathVariable Long scheduleId, @RequestBody AddWorkspaceRequest request) {

        Schedule schedule = validSchedule(scheduleId);
        scheduleCommandService.AddWorkspace(schedule, request);

        return ResponseEntity.ok(ApiResponse.success("워크스페이스를 등록했습니다."));
    }

    // 공통 워크스페이스 삭제
    @Operation(summary = "워크스페이스 삭제", description = "워크스페이스 삭제를 진행합니다.")
    @PostMapping("/delete-workspace/{workspaceId}")
    public ResponseEntity<ApiResponse<DeleteWorkSpaceResponse>> createWorkspace(
        @PathVariable Long workspaceId) {

        Workspace workspace = scheduleQueryService.findWorkspaceById(workspaceId);

        scheduleCommandService.deleteWorkspace(workspace.getId());

        return ResponseEntity.ok(ApiResponse.success("워크스페이스를 삭제했습니다."));
    }
}