package com.grepp.spring.app.controller.api.group;


import com.grepp.spring.app.controller.api.group.payload.request.ControlGroupRoleRequest;
import com.grepp.spring.app.controller.api.group.payload.request.CreateGroupRequest;
import com.grepp.spring.app.controller.api.group.payload.request.ModifyGroupInfoRequest;
import com.grepp.spring.app.controller.api.group.payload.request.ScheduleToGroupRequest;
import com.grepp.spring.app.controller.api.group.payload.response.ControlGroupRoleResponse;
import com.grepp.spring.app.controller.api.group.payload.response.CreateGroupResponse;
import com.grepp.spring.app.controller.api.group.payload.response.DeleteGroupResponse;
import com.grepp.spring.app.controller.api.group.payload.response.DeportGroupMemberResponse;
import com.grepp.spring.app.controller.api.group.payload.response.InviteGroupMemberResponse;
import com.grepp.spring.app.controller.api.group.payload.response.ModifyGroupInfoResponse;
import com.grepp.spring.app.controller.api.group.payload.response.ScheduleToGroupResponse;
import com.grepp.spring.app.controller.api.group.payload.response.ShowCandidateGroupResponse;
import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupMemberResponse;
import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupScheduleResponse;
import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupStatisticsResponse;
import com.grepp.spring.app.controller.api.group.payload.response.WithdrawGroupResponse;
import com.grepp.spring.app.model.group.service.GroupCommandService;
import com.grepp.spring.app.model.group.service.GroupQueryService;
import com.grepp.spring.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/api/v1/groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupController {

    private final GroupCommandService groupCommandService;
    private final GroupQueryService groupQueryService;


    // 그룹 내 일정 조회
    @Operation(summary = "그룹 내 일정 조회")
    @GetMapping("/schedule-groups/{id}")
    public ResponseEntity<ApiResponse<ShowGroupScheduleResponse>> getGroupSchedules(
        @PathVariable Long id
    ) {
        // 그룹 일정 조회
        ShowGroupScheduleResponse response = groupQueryService.displayGroupSchedule(
            id);
        // 그룹 일정 조회 성공
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // 그룹 멤버 조회
    @Operation(summary = "그룹 멤버 조회")
    @GetMapping("/{id}/member")
    public ResponseEntity<ApiResponse<ShowGroupMemberResponse>> getGroupMembers(
        @PathVariable Long id
    ) {
        // 그룹 멤버 조회
        ShowGroupMemberResponse response = groupQueryService.displayGroupMember(id);
        // 그룹 멤버 조회 성공
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // 그룹 통계 조회
    @Operation(summary = "그룹 통계 조회")
    @GetMapping("/{id}/statistics")
    public ResponseEntity<ApiResponse<ShowGroupStatisticsResponse>> getGroupStatistics(
        @PathVariable Long id
    ) {
        // 그룹 통게 조회
        ShowGroupStatisticsResponse response = groupQueryService.displayStatistics(
            id);
        // 그룹 통계 조회 성공
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // 일회성 일정 -> 그룹 일정으로 이동 가능한 그룹 조회
    @Operation(summary = "일회성 일정을 편입시킬 수 있는 그룹 조회")
    @GetMapping("/move-schedule/{id}")
    public ResponseEntity<ApiResponse<ShowCandidateGroupResponse>> showGroupCandidate(
        @PathVariable Long id
    ) {
        // 일회성 일정 -> 그룹 일정으로 이동
        ShowCandidateGroupResponse response = groupQueryService.transferCandidateSchedule(
            id);
        // 일회성 일정 -> 그룹 일정으로 이동 성공
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // 그룹 생성
    @Operation(summary = "그룹 생성")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateGroupResponse>> createGroup(
        @Valid @RequestBody CreateGroupRequest request
    ) {
        // 그룹 생성
        CreateGroupResponse response = groupCommandService.registGroup(request);
        // 그룹 생성 성공
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // 그룹 멤버 추가
    @Operation(summary = "그룹 멤버 추가")
    @PostMapping("/{id}/member")
    public ResponseEntity<ApiResponse<InviteGroupMemberResponse>> addGroupMembers(
        @PathVariable Long id
    ) {
        // 그룹 멤버 추가
        InviteGroupMemberResponse response = groupCommandService.addGroupMember(id);
        // 그룹 멤버 추가 성공
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // 그룹 삭제
    @Operation(summary = "그룹 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteGroupResponse>> deleteGroup(
        @PathVariable Long id
    ) {
        // 그룹 삭제
        groupCommandService.deleteGroup(id);
        // 그룹 삭제 성공
        return ResponseEntity.ok(ApiResponse.success("그룹이 삭제되었습니다."));
    }


    // 그룹 정보 수정
    @Operation(summary = "그룹 정보 수정")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ModifyGroupInfoResponse>> updateGroupInfo(
        @PathVariable Long id,
        @RequestBody ModifyGroupInfoRequest request
    ) {
        // 그룹 정보 수정
        ModifyGroupInfoResponse response = groupCommandService.modifyGroup(id, request);
        // 그룹 정보 수정 성공
        return ResponseEntity.ok(
            ApiResponse.success(response));
    }


    // 그룹 멤버 내보내기
    @Operation(summary = "그룹 멤버 내보내기")
    @PatchMapping("/{groupId}/members/{userId}")
    public ResponseEntity<ApiResponse<DeportGroupMemberResponse>> deportGroupMember(
        @PathVariable Long groupId,
        @PathVariable String userId
    ) {
        // 그룹 멤버 내보내기
        groupCommandService.deportMember(groupId, userId);
        // 그룹 멤버 내보내기 성공
        return ResponseEntity.ok(ApiResponse.success("그룹에서 해당 유저를 내보냈습니다."));
    }


    // 그룹 멤버 권한 관리
    @Operation(summary = "그룹 멤버 권한 관리")
    @PatchMapping("/{id}/members")
    public ResponseEntity<ApiResponse<ControlGroupRoleResponse>> controlGroupRoles(
        @PathVariable Long id,
        @RequestBody ControlGroupRoleRequest request
    ) {
        // 그룹 멤버 권한 관리
        groupCommandService.modifyGroupRole(id, request);
        // 그룹 멤버 권한 관리 성공
        return ResponseEntity.ok(ApiResponse.success("해당 유저의 권한이 재설정 되었습니다."));
    }


    // 일회성 일정 -> 그룹 일정으로 이동
    @Operation(summary = "일회성 일정을 그룹 일정으로 변경")
    @PatchMapping("/move-schedule")
    public ResponseEntity<ApiResponse<ScheduleToGroupResponse>> moveScheduleToGroup(
        @RequestBody ScheduleToGroupRequest request
    ) {
        groupCommandService.transferSchedule(request);
        // 일회성 일정 -> 그룹 일정으로 이동 성공
        return ResponseEntity.ok(ApiResponse.success("일회성 일정에서 그룹으로 바뀌었습니다."));
    }


    // 그룹 탈퇴
    @Operation(summary = "그룹 탈퇴")
    @PatchMapping("/{id}/leave")
    public ResponseEntity<ApiResponse<WithdrawGroupResponse>> withdrawGroup(
        @PathVariable Long id
    ) {
        // 그룹 탈퇴
        groupCommandService.withdrawGroup(id);
        // 그룹 탈퇴 성공
        return ResponseEntity.ok(ApiResponse.success("그룹에서 탈퇴하였습니다."));
    }

}