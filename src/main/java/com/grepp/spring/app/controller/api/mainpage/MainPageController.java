package com.grepp.spring.app.controller.api.mainpage;

import com.grepp.spring.app.controller.api.mainpage.payload.ShowMainPageResponse;
import com.grepp.spring.app.controller.api.mainpage.payload.ShowMainPageResponse.CalendarScheduleList;
import com.grepp.spring.app.controller.api.mainpage.payload.ShowMainPageResponse.GroupList;
import com.grepp.spring.app.controller.api.mainpage.payload.ShowMainPageResponse.ScheduleList;
import com.grepp.spring.app.controller.api.mainpage.payload.ShowMainPageResponse.WeeklySchedules;
import com.grepp.spring.app.model.schedule.domain.MEETING_PLATFORM;
import com.grepp.spring.app.model.schedule.domain.MEETING_TYPE;
import com.grepp.spring.app.model.schedule.domain.SCHEDULES_STATUS;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MainPageController {


  // 통합된 하나의 API
  @Operation(summary = "메인페이지", description = "회원의 그룹리스트, 일정 및 캘린더 조회")
  @GetMapping("/main-page/{memberId}")
  public ResponseEntity<ApiResponse<ShowMainPageResponse>> showMainPage(
      @PathVariable String memberId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    try {

      // 유효성 검사 먼저
      if (!"KAKAO_1234".equals(memberId)) {
        return ResponseEntity.status(404)
            .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 멤버를 찾을 수 없습니다."));
      }
      if (date == null) {
        return ResponseEntity.status(400)
            .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "날짜를 지정해 주세요."));
      }

      if (!(date.equals(LocalDate.of(2025, 7, 3)) || date.equals(LocalDate.of(2025, 7, 4)))) {
        return ResponseEntity.status(404)
            .body(ApiResponse.error(ResponseCode.NOT_FOUND, "해당 날짜의 일정이 없습니다."));
      }

      // 그룹 데이터 생성
      List<GroupList> groupInfos = createGroupList();

      // 일정 데이터 생성
      List<ScheduleList> schedules = createScheduleList();

      // 외부 캘린더 데이터 생성 (구글/내부 일정 분리)
      ShowMainPageResponse calendarData = createCalendarSchedules(date);

      // 통합 응답 생성
      ShowMainPageResponse response = new ShowMainPageResponse();
      response.setGroupInfos(groupInfos);
      response.setSchedules(schedules);
      response.setMessage(calendarData.getMessage());
      response.setGoogleSchedules(calendarData.getGoogleSchedules());
      response.setInternalSchedules(calendarData.getInternalSchedules());

      return ResponseEntity.ok(ApiResponse.success(response));

    } catch (AuthenticationException e) {
      return ResponseEntity.status(401).body(ApiResponse.error(ResponseCode.UNAUTHORIZED));

    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."));
    }
  }

  // 그룹 리스트 mock data
  private List<GroupList> createGroupList() {
    List<GroupList> groupInfos = new ArrayList<>();

    GroupList group1 = new GroupList();
    group1.setGroupId(10000L);
    group1.setGroupName("HMD");
    group1.setGroupId(10000L);
    group1.setDescription("Q3 계획 수립을 위한 팀 미팅 1");
    group1.setMeetingType(MEETING_TYPE.OFFLINE);
    group1.setMaxMember(5);
    group1.setCurrentMember(3);
    group1.setCreatedAt(LocalDateTime.of(2025, 7, 5, 9, 0));
    group1.setIsGroupEvent(true);
    group1.setProfileImageNumber(1);

    GroupList group2 = new GroupList();
    group2.setGroupId(null);
    group2.setGroupName("LKH");
    group2.setGroupId(10000L);
    group2.setDescription("Q3 계획 수립을 위한 팀 미팅 1");
    group2.setMeetingType(MEETING_TYPE.ONLINE);
    group2.setMaxMember(10);
    group2.setCurrentMember(7);
    group2.setCreatedAt(LocalDateTime.of(2025, 7, 8, 16, 30));
    group2.setIsGroupEvent(false);
    group2.setProfileImageNumber(2);


    GroupList group3 = new GroupList();
    group3.setGroupId(10001L); // 중복된 ID 수정
    group3.setGroupName("백엔드 DOD");
    group3.setGroupId(10000L);
    group3.setDescription("이때어때를 위한 팀 회의");
    group3.setMeetingType(MEETING_TYPE.OFFLINE);
    group3.setMaxMember(10);
    group3.setCurrentMember(5);
    group3.setCreatedAt(LocalDateTime.of(2025, 7, 13, 9, 30));
    group3.setIsGroupEvent(true);
    group3.setProfileImageNumber(9);


    groupInfos.add(group1);
    groupInfos.add(group2);
    groupInfos.add(group3);

    return groupInfos;
  }

  // 일정 mock data
  private List<ScheduleList> createScheduleList() {
    List<ScheduleList> schedules = new ArrayList<>();

    ScheduleList schedule1 = new ScheduleList();
    schedule1.setScheduleId(30000L);
    schedule1.setGroupId(10000L);
    schedule1.setName("팀 회의");
    schedule1.setMeetingType(MEETING_TYPE.OFFLINE);
    schedule1.setStartTime(LocalDateTime.of(2025, 7, 8, 14, 0));
    schedule1.setEndTime(LocalDateTime.of(2025, 7, 8, 16, 0));
    schedule1.setSchedulesStatus(SCHEDULES_STATUS.FIXED);
    schedule1.setDescription("주간 팀 회의 및 프로젝트 진행 상황 점검");
    schedule1.setLocation("회의실 A");
    schedule1.setIsGrouped(true);
    schedule1.setGroupName("HMD");

    ScheduleList schedule2 = new ScheduleList();
    schedule2.setScheduleId(30001L);
    schedule2.setGroupId(10001L);
    schedule2.setName("DOD 회의");
    schedule2.setMeetingType(MEETING_TYPE.ONLINE);
    schedule2.setMeetingPlatform(MEETING_PLATFORM.ZOOM);
    schedule2.setStartTime(LocalDateTime.of(2025, 7, 13, 9, 0));
    schedule2.setEndTime(LocalDateTime.of(2025, 7, 13, 13, 0));
    schedule2.setSchedulesStatus(SCHEDULES_STATUS.FIXED);
    schedule2.setDescription("주말 백엔드 회의");
    schedule2.setIsGrouped(true);
    schedule2.setGroupName("BE_DOD");

    schedules.add(schedule1);
    schedules.add(schedule2);

    return schedules;
  }

  List<ShowMainPageResponse.ScheduleList> schedules = new ArrayList<>();

  // 캘린더 mock data


  private List<CalendarScheduleList> createMockGoogleSchedules() {
    List<CalendarScheduleList> googleSchedules = new ArrayList<>();

    // 7월 구글 일정 데이터
    CalendarScheduleList googleSchedule1 = new CalendarScheduleList();
    googleSchedule1.setScheduleId(30001L);
    googleSchedule1.setMemberId("KAKAO_1234");
    googleSchedule1.setCalendarId(300L);
    googleSchedule1.setName("백엔드 회의");
    googleSchedule1.setDescription("스프링 프로젝트 논의");
    googleSchedule1.setStartTime(LocalDateTime.of(2025, 7, 2, 15, 0));
    googleSchedule1.setEndTime(LocalDateTime.of(2025, 7, 2, 16, 0));
    googleSchedule1.setLocation("구글 미트");
    googleSchedule1.setMeetingType(MEETING_TYPE.OFFLINE);

    CalendarScheduleList googleSchedule2 = new CalendarScheduleList();
    googleSchedule2.setScheduleId(30002L);
    googleSchedule2.setMemberId("KAKAO_1234");
    googleSchedule2.setCalendarId(301L);
    googleSchedule2.setName("프론트엔드 리뷰");
    googleSchedule2.setDescription("UI/UX 검토");
    googleSchedule2.setStartTime(LocalDateTime.of(2025, 7, 3, 10, 0));
    googleSchedule2.setEndTime(LocalDateTime.of(2025, 7, 3, 11, 30));
    googleSchedule2.setMeetingType(MEETING_TYPE.ONLINE);

    CalendarScheduleList googleSchedule4 = new CalendarScheduleList();
    googleSchedule4.setScheduleId(30004L);
    googleSchedule4.setMemberId("KAKAO_1234");
    googleSchedule4.setCalendarId(303L);
    googleSchedule4.setName("월말 보고");
    googleSchedule4.setDescription("7월 월말 보고");
    googleSchedule4.setStartTime(LocalDateTime.of(2025, 7, 31, 16, 0));
    googleSchedule4.setEndTime(LocalDateTime.of(2025, 7, 31, 17, 0));
    googleSchedule4.setLocation("대회의실");
    googleSchedule4.setMeetingType(MEETING_TYPE.OFFLINE);

    // 8월 초 일정 (7월 마지막 주에 포함)
    CalendarScheduleList googleSchedule5 = new CalendarScheduleList();
    googleSchedule5.setScheduleId(30005L);
    googleSchedule5.setMemberId("KAKAO_1234");
    googleSchedule5.setCalendarId(304L);
    googleSchedule5.setName("8월 킥오프");
    googleSchedule5.setDescription("8월 프로젝트 시작");
    googleSchedule5.setStartTime(LocalDateTime.of(2025, 8, 1, 9, 0));
    googleSchedule5.setEndTime(LocalDateTime.of(2025, 8, 1, 10, 0));
    googleSchedule5.setMeetingType(MEETING_TYPE.ONLINE);

    googleSchedules.addAll(Arrays.asList(
        googleSchedule1, googleSchedule2, googleSchedule4, googleSchedule5
    ));

    return googleSchedules;
  }

  private ShowMainPageResponse createCalendarSchedules(LocalDate date) {
    List<CalendarScheduleList> googleSchedules = createMockGoogleSchedules();
    List<CalendarScheduleList> internalSchedules = createMockInternalSchedules();

    ShowMainPageResponse response = new ShowMainPageResponse();
    response.setMessage("일정이 정상적으로 조회되었습니다.");
    response.setGoogleSchedules(googleSchedules);
    response.setInternalSchedules(internalSchedules);
    response.setWeeklySchedules(createMonthlyWeeklySchedules(date, googleSchedules, internalSchedules));

    return response;
  }


  private List<CalendarScheduleList> createMockInternalSchedules() {
    List<CalendarScheduleList> internalSchedules = new ArrayList<>();

    // 7월 내부 일정 데이터
    CalendarScheduleList internalSchedule1 = new CalendarScheduleList();
    internalSchedule1.setScheduleId(30003L);
    internalSchedule1.setMemberId("KAKAO_1234");
    internalSchedule1.setCalendarId(300L);
    internalSchedule1.setName("team hmd 정모");
    internalSchedule1.setDescription("팀 정기 모임");
    internalSchedule1.setStartTime(LocalDateTime.of(2025, 7, 3, 10, 0));
    internalSchedule1.setEndTime(LocalDateTime.of(2025, 7, 3, 12, 0));
    internalSchedule1.setLocation("강남역");
    internalSchedule1.setMeetingType(MEETING_TYPE.OFFLINE);

    CalendarScheduleList internalSchedule2 = new CalendarScheduleList();
    internalSchedule2.setScheduleId(30005L);
    internalSchedule2.setMemberId("KAKAO_1234");
    internalSchedule2.setCalendarId(301L);
    internalSchedule2.setName("코드 리뷰");
    internalSchedule2.setDescription("주간 코드 리뷰 세션");
    internalSchedule2.setStartTime(LocalDateTime.of(2025, 7, 4, 14, 0));
    internalSchedule2.setEndTime(LocalDateTime.of(2025, 7, 4, 15, 30));
    internalSchedule2.setLocation("개발실");
    internalSchedule2.setMeetingType(MEETING_TYPE.OFFLINE);

    CalendarScheduleList internalSchedule3 = new CalendarScheduleList();
    internalSchedule3.setScheduleId(30012L);
    internalSchedule3.setMemberId("KAKAO_1234");
    internalSchedule3.setCalendarId(312L);
    internalSchedule3.setName("팀 빌딩");
    internalSchedule3.setDescription("월간 팀 빌딩 활동");
    internalSchedule3.setStartTime(LocalDateTime.of(2025, 7, 18, 18, 0));
    internalSchedule3.setEndTime(LocalDateTime.of(2025, 7, 18, 21, 0));
    internalSchedule3.setLocation("홍대");
    internalSchedule3.setMeetingType(MEETING_TYPE.OFFLINE);
    

    CalendarScheduleList internalSchedule4 = new CalendarScheduleList();
    internalSchedule4.setScheduleId(30013L);
    internalSchedule4.setMemberId("KAKAO_1234");
    internalSchedule4.setCalendarId(313L);
    internalSchedule4.setName("스프린트 회고");
    internalSchedule4.setDescription("7월 스프린트 회고");
    internalSchedule4.setStartTime(LocalDateTime.of(2025, 7, 25, 16, 0));
    internalSchedule4.setEndTime(LocalDateTime.of(2025, 7, 25, 17, 0));
    internalSchedule4.setLocation("회의실 C");
    internalSchedule4.setMeetingType(MEETING_TYPE.OFFLINE);

    internalSchedules.addAll(
        Arrays.asList(internalSchedule1, internalSchedule2, internalSchedule3, internalSchedule4));
    return internalSchedules;
  }

  private List<WeeklySchedules> createMonthlyWeeklySchedules(LocalDate targetDate,
      List<CalendarScheduleList> googleSchedules,
      List<CalendarScheduleList> internalSchedules) {
    List<WeeklySchedules> weeklySchedulesList = new ArrayList<>();

    // 해당 월의 첫 번째 날과 마지막 날 구하기
    LocalDate firstDayOfMonth = targetDate.withDayOfMonth(1);
    LocalDate lastDayOfMonth = targetDate.withDayOfMonth(targetDate.lengthOfMonth());

    // 해당 월의 첫 번째 주 월요일 구하기
    LocalDate firstMonday = firstDayOfMonth.minusDays(
        firstDayOfMonth.getDayOfWeek().getValue() - 1);

    // 해당 월의 마지막 주 일요일 구하기
    LocalDate lastSunday = lastDayOfMonth.plusDays(7 - lastDayOfMonth.getDayOfWeek().getValue());

    // 주별로 일정 생성
    LocalDate currentWeekStart = firstMonday;
    int weekNumber = 1;

    while (!currentWeekStart.isAfter(lastSunday)) {
      LocalDate currentWeekEnd = currentWeekStart.plusDays(6);

      // 람다에서 사용할 별도 final 변수 선언
      final LocalDate weekStart = currentWeekStart;
      final LocalDate weekEnd = currentWeekEnd;

      // 해당 주의 구글 일정 필터링
      List<CalendarScheduleList> weeklyGoogleSchedules = googleSchedules.stream()
          .filter(schedule -> {
            LocalDate scheduleDate = schedule.getStartTime().toLocalDate();
            return !scheduleDate.isBefore(weekStart) && !scheduleDate.isAfter(weekEnd);
          })
          .collect(Collectors.toList());

      // 해당 주의 내부 일정 필터링
      List<CalendarScheduleList> weeklyInternalSchedules = internalSchedules.stream()
          .filter(schedule -> {
            LocalDate scheduleDate = schedule.getStartTime().toLocalDate();
            return !scheduleDate.isBefore(weekStart) && !scheduleDate.isAfter(weekEnd);
          })
          .collect(Collectors.toList());

      // 주간 일정 객체 생성
      WeeklySchedules weeklySchedules = new WeeklySchedules();
      weeklySchedules.setWeekNumber(weekNumber);
      weeklySchedules.setWeekStartDate(currentWeekStart);
      weeklySchedules.setWeekEndDate(currentWeekEnd);
      weeklySchedules.setGoogleSchedules(weeklyInternalSchedules);
      weeklySchedules.setInternalSchedules(weeklyInternalSchedules);

      weeklySchedulesList.add(weeklySchedules);

      // 다음 주로 이동
      currentWeekStart = currentWeekStart.plusWeeks(1);
      weekNumber++;

    }

    return weeklySchedulesList;
  }
}


