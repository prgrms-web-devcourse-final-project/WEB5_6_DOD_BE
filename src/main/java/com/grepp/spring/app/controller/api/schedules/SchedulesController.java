package com.grepp.spring.app.controller.api.schedules;

import com.grepp.spring.app.controller.api.schedules.payload.CreateDepartLocationRequest;
import com.grepp.spring.app.controller.api.schedules.payload.CreateDepartLocationResponse;
import com.grepp.spring.app.controller.api.schedules.payload.CreateOfflineDetailLocationsRequest;
import com.grepp.spring.app.controller.api.schedules.payload.CreateOfflineDetailLocationsResponse;
import com.grepp.spring.app.controller.api.schedules.payload.CreateOnlineMeetingRequest;
import com.grepp.spring.app.controller.api.schedules.payload.CreateOnlineMeetingResponse;
import com.grepp.spring.app.controller.api.schedules.payload.CreateSchedulesRequest;
import com.grepp.spring.app.controller.api.schedules.payload.CreateSchedulesResponse;
import com.grepp.spring.app.controller.api.schedules.payload.CreateWorkspaceRequest;
import com.grepp.spring.app.controller.api.schedules.payload.CreateWorkspaceResponse;
import com.grepp.spring.app.controller.api.schedules.payload.DeleteSchedulesResponse;
import com.grepp.spring.app.controller.api.schedules.payload.DeleteWorkSpaceResponse;
import com.grepp.spring.app.controller.api.schedules.payload.ModifySchedulesRequest;
import com.grepp.spring.app.controller.api.schedules.payload.ModifySchedulesResponse;
import com.grepp.spring.app.controller.api.schedules.payload.ShowMiddleLocationResponse;
import com.grepp.spring.app.controller.api.schedules.payload.ShowSchedulesResponse;
import com.grepp.spring.app.controller.api.schedules.payload.ShowSuggestedLocationsResponse;
import com.grepp.spring.app.controller.api.schedules.payload.VoteMiddleLocationsRequest;
import com.grepp.spring.app.controller.api.schedules.payload.VoteMiddleLocationsResponse;
import com.grepp.spring.app.model.schedule.domain.MEETING_PLATFORM;
import com.grepp.spring.app.model.schedule.domain.SCHEDULES_STATUS;
import com.grepp.spring.app.model.schedule.domain.VOTE_STATUS;
import com.grepp.spring.infra.error.exceptions.AuthApiException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

@RestController
@RequestMapping("/api/v1/schedules")
public class SchedulesController {

    // 일정 등록
    @Operation(summary = "일정 등록", description = "일정 등록을 진행합니다.")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateSchedulesResponse>> createSchedules(@RequestBody CreateSchedulesRequest request) {

        try {
            if(
                request.getEventId()!= 20000L && request.getEventId()!=20001L && request.getEventId()!=20002L &&
                    request.getEventId()!=20003L && request.getEventId()!=20004L && request.getEventId()!=22222L
            ){
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다. eventId는 20000 ~ 20004 입니다."));
            }


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

    //NOTE
    // 일정 수정
    @Operation(summary = "일정 수정", description = "일정 수정을 진행합니다.")
    @PatchMapping("/modify/{scheduleId}") // 일정 수정 관련된 것들은 모두 수행. 그럼 patch가 맞는가?
    public ResponseEntity<ApiResponse<ModifySchedulesResponse>> modifyScedules(@PathVariable Long scheduleId, @RequestBody ModifySchedulesRequest request) {
        try {

            if (request.getEventId() !=20000 && request.getEventId() !=20001 && request.getEventId() !=20002 && request.getEventId() !=20003 && request.getEventId() !=20004 && request.getEventId() !=22222 ) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다. eventId는 20000 ~ 20004 입니다."));
            }

            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
            }
            return ResponseEntity.ok(ApiResponse.success("일정이 수정되었습니다."));
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

    // 일정 정보 조회
    @Operation(summary = "일정 정보 조회", description = "일정 정보를 조회합니다.")
    @GetMapping("/show/{scheduleId}")
    public ResponseEntity<ApiResponse<ShowSchedulesResponse>> showSchedules(@PathVariable Long scheduleId, @RequestParam Long eventId) {

        try {

            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
            }

            if (eventId !=20000 && eventId !=20001 && eventId !=20002 && eventId !=20003 && eventId !=20004 && eventId !=22222 ) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다. eventId는 20000 ~ 20004 입니다."));
            }

            ShowSchedulesResponse  response = new ShowSchedulesResponse();
            response.setEventId(20000L);
            response.setStartTime(LocalDateTime.of(2025, 7, 6, 3, 7));
            response.setEndTime(LocalDateTime.of(2025, 7, 7, 3, 7));
            response.setSCHEDULES_STATUS(SCHEDULES_STATUS.FIXED);
            response.setLocation("강남역");
            response.setSpecificLocation("강남역 스타벅스");
            response.setDescription("DOD의 즐거운 미팅 날");
            response.setMeetingPlatform(MEETING_PLATFORM.ZOOM);
            response.setPlatformUrl("https://zoom.us/test-meeting");

            response.setMembers(List.of("이서준","이강현","안준희","정서윤","최동준","박상윤","박은서","박준규","현혜주","황수지","아이유","박보검"));
            response.setWorkspacesUrl(List.of("www.notion.com","www.github.com","www.slack.com"));
            response.setWorkspacesName(List.of("프론트엔드 기획서","이때 어때 레포지토리","데브코스 슬렉"));
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

    // 일정 삭제
    @Operation(summary = "일정 삭제", description = "일정을 삭제합니다.")
    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<ApiResponse<DeleteSchedulesResponse>> deleteSchedules(@PathVariable Long scheduleId) {

        try {
            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
            }
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

    // 출발장소 등록
    @Operation(summary = "출발장소 등록", description = "출발장소 등록을 진행합니다.")
    @PostMapping("create-depart-location")
    public ResponseEntity<ApiResponse<CreateDepartLocationResponse>> createDepartLocation(@RequestBody CreateDepartLocationRequest request) {

        try {

            if(
                request.getScheduleId()!= 30000L && request.getScheduleId()!=30001L && request.getScheduleId()!=30002L &&
                    request.getScheduleId()!=30003L && request.getScheduleId()!=30005L && request.getScheduleId()!=30303L && request.getScheduleId()!=33333L
            ){
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
            }

            return ResponseEntity.ok(ApiResponse.success("출발장소가 등록되었습니다."));
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

    // 중간장소 후보 조회
    @Operation(summary = "중간장소 후보 조회", description = "중간장소 후보를 조회합니다.")
    @GetMapping("/show-suggested-locations{scheduleId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> showSuggestedLocations(@PathVariable Long scheduleId) {

        try {
            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
            }

            ShowSuggestedLocationsResponse response1 = new ShowSuggestedLocationsResponse();
            response1.setLocationName("동대문역사문화공원역");
            response1.setLatitude(37.4979);
            response1.setLongitude(127.0276);
            response1.setSuggestedMemberId(1L);
            response1.setVoteCount(5L);
            response1.setSCHEDULES_STATUS(VOTE_STATUS.ALMOST);
            response1.setMetroLines(Arrays.asList("2", "4", "5"));
            response1.setStationColors(Arrays.asList("G222","B342","P234"));


            ShowSuggestedLocationsResponse response2 = new ShowSuggestedLocationsResponse();
            response2.setLocationName("역삼역");
            response2.setLatitude(37.5008);
            response2.setLongitude(127.0365);
            response2.setSuggestedMemberId(2L);
            response2.setVoteCount(2L);
            response2.setSCHEDULES_STATUS(VOTE_STATUS.WINNER);
            response2.setMetroLines(Arrays.asList("2","8"));
            response2.setStationColors(Arrays.asList("G222","R342"));


            ShowSuggestedLocationsResponse response3 = new ShowSuggestedLocationsResponse();
            response3.setLocationName("홍대입구역");
            response3.setLatitude(37.5572);
            response3.setLongitude(126.9245);
            response3.setSuggestedMemberId(3L);
            response3.setVoteCount(8L);
            response3.setSCHEDULES_STATUS(VOTE_STATUS.DEFAULT);
            response3.setMetroLines(Arrays.asList("2","5","경의중앙","수인분당"));
            response3.setStationColors(Arrays.asList("G222","P234","b12314","y097234"));


            List<ShowSuggestedLocationsResponse> list = Arrays.asList(response1, response2, response3);

            Map<String, Object> result = new HashMap<>();
            result.put("noVoteCount", 2);
            result.put("data", list);
            return ResponseEntity.ok(ApiResponse.success(result));
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

    // 출발 장소 지점 투표하기
    @Operation(summary = "출발 장소 지점 투표하기", description = "출발 장소를 투표합니다.")
    @PostMapping("/suggested-locations/vote/{scheduleId}")
    public ResponseEntity<ApiResponse<VoteMiddleLocationsResponse>> voteMiddleLocation(@PathVariable Long scheduleId, @RequestBody VoteMiddleLocationsRequest request) {

        try {

            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
            }

            if (request.getLocationId() !=40000 && request.getLocationId() !=40001 && request.getLocationId() !=40002 && request.getLocationId() !=40003 && request.getLocationId() !=40004 && request.getLocationId() !=40404 && request.getLocationId() !=44444 ) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 투표리스트(장소)를 찾을 수 없습니다. locationId는 40000 ~ 40004 입니다."));
            }

            return ResponseEntity.ok(ApiResponse.success("성공적으로 투표를 진행했습니다."));
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

    // 중간 장소(지하철 역) 지점 확인 && 중간 장소 지점 투표결과 조회
    @Operation(summary = "중간 장소(지하철 역) 지점 확인 && 중간 장소 지점 투표결과 조회", description = "중간 장소(지하철 역) 지점 확인 && 중간 장소 지점 투표결과 조회")
    @GetMapping("/show-middle-location/{scheduleId}")
    public ResponseEntity<ApiResponse<ShowMiddleLocationResponse>> showMiddleLocation(@PathVariable Long scheduleId) {

        try {

            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
            }

            ShowMiddleLocationResponse response = new ShowMiddleLocationResponse();
            response.setLocationName("잠실역");
            response.setLatitude(37.5572);
            response.setLongitude(126.9245);
            response.setVoteCount(8L);
            response.setMetroLines(List.of("2","8"));
            response.setStationColors(List.of("G222","R342"));
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

    // 온라인 회의장 링크 개설(줌, 구글미트)
    @Operation(summary = "온라인 회의장 링크 개설(줌, 구글미트)", description = "온라인 회의장을 개설합니다.")
    @PostMapping("/create-online-meeting/{scheduleId}")
    public ResponseEntity<ApiResponse<CreateOnlineMeetingResponse>> CreateOnlineMeeting(@PathVariable Long scheduleId, @RequestBody CreateOnlineMeetingRequest request) {

        try {

            if(
                scheduleId != 30000L && scheduleId !=30001L && scheduleId !=30002L && scheduleId != 30003L
            ){
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "scheduleId는 30000 ~ 30003 입니다."));
            }

            CreateOnlineMeetingResponse response = new CreateOnlineMeetingResponse();
                response.setMeetingPlatformCreate(MEETING_PLATFORM.GOOGLE_MEET);

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

    // 오프라인 세부 장소 생성
    @Operation(summary = "오프라인 세부 장소 생성", description = "오프라인 세부 장소 생성을 진행합니다.")
    @PostMapping("/create-detail-locations/{scheduleId}")
    public ResponseEntity<ApiResponse<CreateOfflineDetailLocationsResponse>> CreateOfflineDetailLocation(@PathVariable Long scheduleId, @RequestBody CreateOfflineDetailLocationsRequest request) {

        try {
            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
            }

            return ResponseEntity.ok(ApiResponse.success("오프라인 세부 장소를 등록했습니다."));
        }

           catch (Exception e) {
            if (e instanceof AuthApiException) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error(ResponseCode.UNAUTHORIZED, "권한이 없습니다."));
            }
            return ResponseEntity.status(400)
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "서버가 요청을 처리할 수 없습니다."));
        }
    }

    // 공통 워크스페이스 등록
    @Operation(summary = "워크스페이스 등록", description = "워크스페이스 등록을 진행합니다.")
    @PostMapping("/add-workspace/{scheduleId}")
    public ResponseEntity<ApiResponse<CreateWorkspaceResponse>> CreateWorkspace(@PathVariable Long scheduleId, @RequestBody CreateWorkspaceRequest request) {

        try {
            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
            }

            return ResponseEntity.ok(ApiResponse.success("워크스페이스를 등록했습니다."));
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

    // 공통 워크스페이스 삭제
    @Operation(summary = "워크스페이스 삭제", description = "워크스페이스 삭제를 진행합니다.")
    @PostMapping("/delete-workspace/{scheduleId}")
    public ResponseEntity<ApiResponse<DeleteWorkSpaceResponse>> CreateWorkspace(@PathVariable Long scheduleId) {
        try {
            if (scheduleId !=30000 && scheduleId !=30001 && scheduleId !=30002 && scheduleId !=30003 && scheduleId !=30005 && scheduleId !=30303 && scheduleId != 33333) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다. scheduleId는 30000 ~ 30003 입니다."));
            }

            return ResponseEntity.ok(ApiResponse.success("워크스페이스를 삭제했습니다."));
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

    }