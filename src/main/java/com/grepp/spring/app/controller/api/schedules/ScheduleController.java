package com.grepp.spring.app.controller.api.schedules;

import com.grepp.spring.app.controller.api.schedules.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.controller.api.schedules.payload.response.CreateSchedulesResponse;
import com.grepp.spring.app.controller.api.schedules.payload.response.DeleteSchedulesResponse;
import com.grepp.spring.app.controller.api.schedules.payload.response.ShowScheduleResponse;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.schedule.entity.Schedule;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    @Autowired private ScheduleCommandService scheduleCommandService;
    @Autowired private ScheduleQueryService scheduleQueryService;

    // 일정 정보 조회
    @Operation(summary = "일정 정보 조회", description = "일정 정보를 조회합니다.")
    @GetMapping("/show/{scheduleId}")
    public ResponseEntity<ApiResponse<ShowScheduleResponse>> showSchedules(@PathVariable Long scheduleId) {

        try {
            Optional<Schedule> sId = scheduleQueryService.findScheduleById(scheduleId);

            if (sId.isEmpty()) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId를 확인해주세요."));
            }

            ShowScheduleResponse response = scheduleQueryService.showSchedule(scheduleId);

            return ResponseEntity.ok(ApiResponse.success(response));
        }

        catch (Exception e) {
            if (e instanceof AuthApiException) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "인증(로그인)이 되어있지 않습니다. 헤더에 Bearer {AccressToken}을 넘겼는지 확인해주세요."));
            }
            return ResponseEntity.status(400)
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
        }
    }

    // 일정 등록
    @Operation(summary = "일정 등록", description = "일정 등록을 진행합니다.")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateSchedulesResponse>> createSchedules(@RequestBody CreateSchedulesRequest request) {

        try {
            Optional<Event> eId = scheduleQueryService.findEventById(request.getEventId());

            if(eId.isEmpty()){
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다. eventId를 확인해주세요."));
            }

            scheduleCommandService.createSchedule(request);

            return ResponseEntity.ok(ApiResponse.success("일정이 등록되었습니다."));
        }
         catch (Exception e) {
             if (e instanceof AuthApiException) {
                 return ResponseEntity.status(401)
                     .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "인증(로그인)이 되어있지 않습니다. 헤더에 Bearer {AccressToken}을 넘겼는지 확인해주세요."));
             }

             return ResponseEntity.status(400)
                 .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
         }
    }

////    //NOTE
////    // 일정 수정
////    @Operation(summary = "일정 수정", description = "일정 수정을 진행합니다.")
////    @PatchMapping("/modify/{scheduleId}") // 일정 수정 관련된 것들은 모두 수행. Pathch는 리소스 일부 수정만 가능. 바꾸고 싶은 필드만 변경가능
////    // request 전체 내용 중 변경된 내용만 반영해야 한다. 그럼 request의 14개의 필드 null 체크를 다 해줘야 하나...?
////    public ResponseEntity<ApiResponse<ModifySchedulesResponse>> modifyScedules(@PathVariable Long scheduleId, @RequestBody ModifySchedulesRequest request) {
//////        try {
////
////            Optional<Event> eId = scheduleService.findEventById(request.getEventId());
////
////            if(eId.isEmpty()){
////                return ResponseEntity.status(404)
////                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다. eventId를 확인해주세요."));
////            }
////
////            Optional<Schedule> sId = scheduleService.findScheduleById(scheduleId);
////
////            if (sId.isEmpty()) {
////                return ResponseEntity.status(404)
////                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId를 확인해주세요."));
////            }
////
////            scheduleService.modifySchedule(scheduleId);
////
////            ShowScheduleResponse  response = new ShowScheduleResponse();
////            response.setEventId(20000L);
////            response.setStartTime(LocalDateTime.of(2025, 7, 6, 3, 7));
////            response.setEndTime(LocalDateTime.of(2025, 7, 7, 3, 7));
//////            response.setSchedulesStatus(SchedulesStatus.FIXED);
////            response.setLocation("강남역");
////            response.setSpecificLocation("강남역 스타벅스");
////            response.setDescription("DOD의 즐거운 미팅 날");
////            response.setMeetingPlatform(MeetingPlatform.ZOOM);
////            response.setPlatformUrl("https://zoom.us/test-meeting");
////
//////         catch (Exception e) {
//////            if (e instanceof AuthApiException) {
//////                return ResponseEntity.status(401)
//////                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "인증(로그인)이 되어있지 않습니다. 헤더에 Bearer {AccressToken}을 넘겼는지 확인해주세요."));
//////            }
//////            return ResponseEntity.status(400)
//////                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
//////        }
////    }
//
    // 일정 삭제
    @Operation(summary = "일정 삭제", description = "일정을 삭제합니다.")
    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<ApiResponse<DeleteSchedulesResponse>> deleteSchedules(@PathVariable Long scheduleId) {

        try {
            Optional<Schedule> sId = scheduleQueryService.findScheduleById(scheduleId);

            if (sId.isEmpty()) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId를 확인해주세요."));
            }
            scheduleCommandService.deleteSchedule(scheduleId);

            return ResponseEntity.ok(ApiResponse.success("일정을 삭제했습니다."));
        }
           catch (Exception e) {
            if (e instanceof AuthApiException) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "인증(로그인)이 되어있지 않습니다. 헤더에 Bearer {AccressToken}을 넘겼는지 확인해주세요."));
            }
            return ResponseEntity.status(400)
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
        }
    }
//
//    // 출발장소 등록
//    @Operation(summary = "출발장소 등록", description = "출발장소 등록을 진행합니다.")
//    @PostMapping("create-depart-location/{scheduleId}")
//    public ResponseEntity<ApiResponse<CreateDepartLocationResponse>> createDepartLocation(@RequestParam Long scheduleId, @RequestBody CreateDepartLocationRequest request) {
//
//        try {
//
//            if(
//                scheduleId != 30000L && scheduleId !=30001L && scheduleId !=30002L &&
//                    scheduleId !=30003L && scheduleId !=30005L && scheduleId !=30303L && scheduleId !=33333L
//            ){
//                return ResponseEntity.status(404)
//                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
//            }
//
//            return ResponseEntity.ok(ApiResponse.success("출발장소가 등록되었습니다."));
//        }
//
//           catch (Exception e) {
//            if (e instanceof AuthApiException) {
//                return ResponseEntity.status(401)
//                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "인증(로그인)이 되어있지 않습니다. 헤더에 Bearer {AccressToken}을 넘겼는지 확인해주세요."));
//            }
//            return ResponseEntity.status(400)
//                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
//        }
//    }
//
//    // 중간장소 후보 조회
//    @Operation(summary = "중간장소 후보 조회", description = "중간장소 후보를 조회합니다.")
//    @GetMapping("/show-suggested-locations{scheduleId}")
//    public ResponseEntity<ApiResponse<Map<String, Object>>> showSuggestedLocations(@PathVariable Long scheduleId) {
//
//        try {
//            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
//                return ResponseEntity.status(404)
//                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
//            }
//
//            ShowSuggestedLocationsResponse response1 = new ShowSuggestedLocationsResponse();
//            response1.setLocationName("동대문역사문화공원역");
//            response1.setLatitude(37.4979);
//            response1.setLongitude(127.0276);
//            response1.setSuggestedMemberId(1L);
//            response1.setVoteCount(5L);
//            response1.setSCHEDULES_STATUS(VoteStatus.ALMOST);
////            response1.setMetroLines(Arrays.asList("2", "4", "5"));
////            response1.setStationColors(Arrays.asList("G222","B342","P234"));
//
//
//            ShowSuggestedLocationsResponse response2 = new ShowSuggestedLocationsResponse();
//            response2.setLocationName("역삼역");
//            response2.setLatitude(37.5008);
//            response2.setLongitude(127.0365);
//            response2.setSuggestedMemberId(2L);
//            response2.setVoteCount(2L);
//            response2.setSCHEDULES_STATUS(VoteStatus.WINNER);
////            response2.setMetroLines(Arrays.asList("2","8"));
////            response2.setStationColors(Arrays.asList("G222","R342"));
//
//
//            ShowSuggestedLocationsResponse response3 = new ShowSuggestedLocationsResponse();
//            response3.setLocationName("홍대입구역");
//            response3.setLatitude(37.5572);
//            response3.setLongitude(126.9245);
//            response3.setSuggestedMemberId(3L);
//            response3.setVoteCount(8L);
//            response3.setSCHEDULES_STATUS(VoteStatus.DEFAULT);
////            response3.setMetroLines(Arrays.asList("2","5","경의중앙","수인분당"));
////            response3.setStationColors(Arrays.asList("G222","P234","b12314","y097234"));
//
//
//            List<ShowSuggestedLocationsResponse> list = Arrays.asList(response1, response2, response3);
//
//            Map<String, Object> result = new HashMap<>();
//            result.put("noVoteCount", 2);
//            result.put("data", list);
//            return ResponseEntity.ok(ApiResponse.success(result));
//        }
//
//           catch (Exception e) {
//            if (e instanceof AuthApiException) {
//                return ResponseEntity.status(401)
//                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "인증(로그인)이 되어있지 않습니다. 헤더에 Bearer {AccressToken}을 넘겼는지 확인해주세요."));
//            }
//            return ResponseEntity.status(400)
//                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
//        }
//    }
//
//    // 출발 장소 지점 투표하기
//    @Operation(summary = "출발 장소 지점 투표하기", description = "출발 장소를 투표합니다.")
//    @PostMapping("/suggested-locations/vote/{scheduleId}")
//    public ResponseEntity<ApiResponse<VoteMiddleLocationsResponse>> voteMiddleLocation(@PathVariable Long scheduleId, @RequestBody VoteMiddleLocationsRequest request) {
//
//        try {
//
//            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
//                return ResponseEntity.status(404)
//                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
//            }
//
//            if (request.getLocationId() !=40000 && request.getLocationId() !=40001 && request.getLocationId() !=40002 && request.getLocationId() !=40003 && request.getLocationId() !=40004 && request.getLocationId() !=40404 && request.getLocationId() !=44444 ) {
//                return ResponseEntity.status(404)
//                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 투표리스트(장소)를 찾을 수 없습니다. locationId는 40000 ~ 40004 입니다."));
//            }
//
//            return ResponseEntity.ok(ApiResponse.success("성공적으로 투표를 진행했습니다."));
//        }
//
//           catch (Exception e) {
//            if (e instanceof AuthApiException) {
//                return ResponseEntity.status(401)
//                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "인증(로그인)이 되어있지 않습니다. 헤더에 Bearer {AccressToken}을 넘겼는지 확인해주세요."));
//            }
//            return ResponseEntity.status(400)
//                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
//        }
//    }
//
//    // 중간 장소(지하철 역) 지점 확인 && 중간 장소 지점 투표결과 조회
//    @Operation(summary = "중간 장소(지하철 역) 지점 확인 && 중간 장소 지점 투표결과 조회", description = "중간 장소(지하철 역) 지점 확인 && 중간 장소 지점 투표결과 조회")
//    @GetMapping("/show-middle-location/{scheduleId}")
//    public ResponseEntity<ApiResponse<ShowMiddleLocationResponse>> showMiddleLocation(@PathVariable Long scheduleId) {
//
//        try {
//
//            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
//                return ResponseEntity.status(404)
//                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
//            }
//
//            ShowMiddleLocationResponse response = new ShowMiddleLocationResponse();
//            response.setLocationName("잠실역");
//            response.setLatitude(37.5572);
//            response.setLongitude(126.9245);
////            response.setVoteCount(8L);
////            response.setMetroLines(List.of("2","8"));
////            response.setStationColors(List.of("G222","R342"));
//            return ResponseEntity.ok(ApiResponse.success(response));
//        }
//
//           catch (Exception e) {
//            if (e instanceof AuthApiException) {
//                return ResponseEntity.status(401)
//                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "인증(로그인)이 되어있지 않습니다. 헤더에 Bearer {AccressToken}을 넘겼는지 확인해주세요."));
//            }
//            return ResponseEntity.status(400)
//                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
//        }
//    }
//
//    // 온라인 회의장 링크 개설(줌, 구글미트)
//    @Operation(summary = "온라인 회의장 링크 개설(줌, 구글미트)", description = "온라인 회의장을 개설합니다.")
//    @PostMapping("/create-online-meeting/{scheduleId}")
//    public ResponseEntity<ApiResponse<CreateOnlineMeetingResponse>> CreateOnlineMeeting(@PathVariable Long scheduleId, @RequestBody CreateOnlineMeetingRequest request) {
//
//        try {
//
//            if(
//                scheduleId != 30000L && scheduleId !=30001L && scheduleId !=30002L && scheduleId != 30003L
//            ){
//                return ResponseEntity.status(404)
//                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "scheduleId는 30000 ~ 30003 입니다."));
//            }
//
//            CreateOnlineMeetingResponse response = new CreateOnlineMeetingResponse();
//                response.setMeetingPlatformCreate(MeetingPlatform.GOOGLE_MEET);
//
//            return ResponseEntity.ok(ApiResponse.success(response));
//        }
//
//           catch (Exception e) {
//            if (e instanceof AuthApiException) {
//                return ResponseEntity.status(401)
//                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "인증(로그인)이 되어있지 않습니다. 헤더에 Bearer {AccressToken}을 넘겼는지 확인해주세요."));
//            }
//            return ResponseEntity.status(400)
//                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
//        }
//    }
//
//    // 오프라인 세부 장소 생성
//    @Operation(summary = "오프라인 세부 장소 생성", description = "오프라인 세부 장소 생성을 진행합니다.")
//    @PostMapping("/create-detail-locations/{scheduleId}")
//    public ResponseEntity<ApiResponse<CreateOfflineDetailLocationsResponse>> CreateOfflineDetailLocation(@PathVariable Long scheduleId, @RequestBody CreateOfflineDetailLocationsRequest request) {
//
//        try {
//            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
//                return ResponseEntity.status(404)
//                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
//            }
//
//            return ResponseEntity.ok(ApiResponse.success("오프라인 세부 장소를 등록했습니다."));
//        }
//
//           catch (Exception e) {
//            if (e instanceof AuthApiException) {
//                return ResponseEntity.status(401)
//                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "권한이 없습니다."));
//            }
//            return ResponseEntity.status(400)
//                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
//        }
//    }
//
//    // 공통 워크스페이스 등록
//    @Operation(summary = "워크스페이스 등록", description = "워크스페이스 등록을 진행합니다.")
//    @PostMapping("/add-workspace/{scheduleId}")
//    public ResponseEntity<ApiResponse<CreateWorkspaceResponse>> CreateWorkspace(@PathVariable Long scheduleId, @RequestBody CreateWorkspaceRequest request) {
//
//        try {
//            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
//                return ResponseEntity.status(404)
//                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
//            }
//
//            return ResponseEntity.ok(ApiResponse.success("워크스페이스를 등록했습니다."));
//        }
//           catch (Exception e) {
//            if (e instanceof AuthApiException) {
//                return ResponseEntity.status(401)
//                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "인증(로그인)이 되어있지 않습니다. 헤더에 Bearer {AccressToken}을 넘겼는지 확인해주세요."));
//            }
//            return ResponseEntity.status(400)
//                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
//        }
//    }
//
//    // 공통 워크스페이스 삭제
//    @Operation(summary = "워크스페이스 삭제", description = "워크스페이스 삭제를 진행합니다.")
//    @PostMapping("/delete-workspace/{scheduleId}")
//    public ResponseEntity<ApiResponse<DeleteWorkSpaceResponse>> CreateWorkspace(@PathVariable Long scheduleId, @RequestBody DeleteWorkSpaceRequest request) {
//        try {
//            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
//                return ResponseEntity.status(404)
//                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
//            }
//
//            return ResponseEntity.ok(ApiResponse.success("워크스페이스를 삭제했습니다."));
//        }
//        catch (Exception e) {
//            if (e instanceof AuthApiException) {
//                return ResponseEntity.status(401)
//                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "인증(로그인)이 되어있지 않습니다. 헤더에 Bearer {AccressToken}을 넘겼는지 확인해주세요."));
//            }
//            return ResponseEntity.status(400)
//                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
//        }
//    }
}