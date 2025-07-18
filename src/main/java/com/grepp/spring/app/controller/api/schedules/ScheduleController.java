package com.grepp.spring.app.controller.api.schedules;

import com.grepp.spring.app.controller.api.schedules.payload.request.CreateDepartLocationRequest;
import com.grepp.spring.app.controller.api.schedules.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.controller.api.schedules.payload.request.AddWorkspaceRequest;
import com.grepp.spring.app.controller.api.schedules.payload.request.ModifySchedulesRequest;
import com.grepp.spring.app.controller.api.schedules.payload.request.VoteMiddleLocationsRequest;
import com.grepp.spring.app.controller.api.schedules.payload.response.CreateDepartLocationResponse;
import com.grepp.spring.app.controller.api.schedules.payload.response.CreateOnlineMeetingRoomResponse;
import com.grepp.spring.app.controller.api.schedules.payload.response.CreateSchedulesResponse;
import com.grepp.spring.app.controller.api.schedules.payload.response.CreateWorkspaceResponse;
import com.grepp.spring.app.controller.api.schedules.payload.response.DeleteSchedulesResponse;
import com.grepp.spring.app.controller.api.schedules.payload.response.DeleteWorkSpaceResponse;
import com.grepp.spring.app.controller.api.schedules.payload.response.ShowScheduleResponse;
import com.grepp.spring.app.controller.api.schedules.payload.response.ShowSuggestedLocationsResponse;
import com.grepp.spring.app.controller.api.schedules.payload.response.VoteMiddleLocationsResponse;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.LocationQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.service.ScheduleCommandService;
import com.grepp.spring.app.model.schedule.service.ScheduleQueryService;
import com.grepp.spring.infra.error.exceptions.AuthApiException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ScheduleController {

    @Autowired
    private ScheduleCommandService scheduleCommandService;
    @Autowired
    private ScheduleQueryService scheduleQueryService;

    @Autowired
    private ScheduleMemberQueryRepository scheduleMemberQueryRepository;
    @Autowired
    private LocationQueryRepository locationQueryRepository;

    // 일정 조회
    @Operation(summary = "일정 조회", description = "일정을 조회합니다.")
    @GetMapping("/show/{scheduleId}")
    public ResponseEntity<ApiResponse<ShowScheduleResponse>> showSchedules(
        @PathVariable Long scheduleId) {

            scheduleQueryService.findScheduleById(scheduleId);
            ShowScheduleResponse response = scheduleQueryService.showSchedule(scheduleId);

            return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 일정 등록
    @Operation(summary = "일정 등록", description = "일정 등록을 진행합니다.")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateSchedulesResponse>> createSchedules(
        @RequestBody CreateSchedulesRequest request) {


            Optional<Event> eId = scheduleQueryService.findEventById(request.getEventId());

            if (eId.isEmpty()) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND,
                        "해당 이벤트를 찾을 수 없습니다. eventId를 확인해주세요."));
            }

            CreateSchedulesResponse response = scheduleCommandService.createSchedule(request);

            return ResponseEntity.ok(ApiResponse.success(response));

    }

    // 일정 수정
    @Operation(summary = "일정 수정", description = "일정 수정을 진행합니다.")
    @PatchMapping("/modify/{scheduleId}")
    // 일정 수정 관련된 것들은 모두 수행. Pathch는 리소스 일부 수정만 가능. 바꾸고 싶은 필드만 변경가능
    // request 전체 내용 중 변경된 내용만 반영해야 한다. 그럼 request의 14개의 필드 null 체크를 다 해줘야 하나...?
    public ResponseEntity<ApiResponse<Void>> modifySchedule(
        @PathVariable Long scheduleId, @RequestBody ModifySchedulesRequest request) {

            scheduleQueryService.findScheduleById(scheduleId);
            scheduleCommandService.modifySchedule(request, scheduleId);

            return ResponseEntity.ok(ApiResponse.success("일정이 수정되었습니다."));
    }


    // 일정 삭제
    @Operation(summary = "일정 삭제", description = "일정을 삭제합니다.")
    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<ApiResponse<DeleteSchedulesResponse>> deleteSchedules(
        @PathVariable Long scheduleId) {

            scheduleQueryService.findScheduleById(scheduleId);
            scheduleCommandService.deleteSchedule(scheduleId);

            return ResponseEntity.ok(ApiResponse.success("일정을 삭제했습니다."));
    }


    // 출발장소 등록
    @Operation(summary = "출발장소 등록", description = "출발장소 등록을 진행합니다.")
    @PostMapping("create-depart-location/{scheduleId}")
    public ResponseEntity<ApiResponse<CreateDepartLocationResponse>> createDepartLocation(
        @RequestParam Long scheduleId, @RequestBody CreateDepartLocationRequest request) {

            scheduleQueryService.findScheduleById(scheduleId);


            scheduleCommandService.createDepartLocation(scheduleId, request);

            return ResponseEntity.ok(ApiResponse.success("출발장소가 등록되었습니다."));
    }

    // 중간장소 후보 조회
    @Operation(summary = "중간장소 후보 조회 & 중간장소 투표결과 확인", description = "중간장소 후보를 조회 & 중간장소 투표결과 확인 합니다.")
    @GetMapping("/show-suggested-locations/{scheduleId}")
    public ResponseEntity<ApiResponse<ShowSuggestedLocationsResponse>> showSuggestedLocations(
        @PathVariable Long scheduleId) {

            scheduleQueryService.findScheduleById(scheduleId);
            ShowSuggestedLocationsResponse response = scheduleQueryService.showSuggestedLocation(scheduleId);

            return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 중간장소 투표하기
    @Operation(summary = "중간장소 투표하기", description = "중간장소를 투표합니다.")
    @PostMapping("/suggested-locations/vote/{scheduleMemberId}")
    public ResponseEntity<ApiResponse<VoteMiddleLocationsResponse>> voteMiddleLocation(
        @PathVariable Long scheduleMemberId, @RequestBody VoteMiddleLocationsRequest request) {

            Schedule sId = scheduleQueryService.findScheduleById(request.getScheduleId());
            Optional<ScheduleMember> smId = scheduleMemberQueryRepository.findById(scheduleMemberId);
            Optional<Location> lId = locationQueryRepository.findById(request.getLocationId());

            if (lId.isEmpty()) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND,
                        "해당 투표리스트(장소)를 찾을 수 없습니다. locationId를 확인해주세요."));
            }

            scheduleCommandService.voteMiddleLocation(smId, lId, sId);

            return ResponseEntity.ok(ApiResponse.success("성공적으로 투표를 진행했습니다."));
    }

    // 온라인 회의장 링크 개설(줌, 구글미트)
    @Operation(summary = "온라인 회의장 링크 개설(줌, 구글미트)", description = "온라인 회의장을 개설합니다.")
    @PostMapping("/create-online-meeting/{scheduleId}")
    public ResponseEntity<ApiResponse<CreateOnlineMeetingRoomResponse>> CreateOnlineMeetingRoom(@PathVariable Long scheduleId) {

            scheduleQueryService.findScheduleById(scheduleId);
            CreateOnlineMeetingRoomResponse response = scheduleCommandService.createOnlineMeeting(scheduleId);

            return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 공통 워크스페이스 등록
    @Operation(summary = "워크스페이스 등록", description = "워크스페이스 등록을 진행합니다.")
    @PostMapping("/add-workspace/{scheduleId}")
    public ResponseEntity<ApiResponse<CreateWorkspaceResponse>> createWorkspace(@PathVariable Long scheduleId, @RequestBody AddWorkspaceRequest request) {

            Schedule sId = scheduleQueryService.findScheduleById(scheduleId);
            scheduleCommandService.AddWorkspace(sId, request);

            return ResponseEntity.ok(ApiResponse.success("워크스페이스를 등록했습니다."));
    }

    // 공통 워크스페이스 삭제
    @Operation(summary = "워크스페이스 삭제", description = "워크스페이스 삭제를 진행합니다.")
    @PostMapping("/delete-workspace/{workspaceId}")
    public ResponseEntity<ApiResponse<DeleteWorkSpaceResponse>> createWorkspace(
        @PathVariable Long workspaceId) {

            scheduleCommandService.deleteWorkspace(workspaceId);

            return ResponseEntity.ok(ApiResponse.success("워크스페이스를 삭제했습니다."));
    }
}