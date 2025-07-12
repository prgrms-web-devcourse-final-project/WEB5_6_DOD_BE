package com.grepp.spring.app.controller.api.mainpage.payload.response;


import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ShowMainPageResponse {

  private List<GroupList> groupInfos;
  private List<ScheduleList> schedules;

  // 캘린더 조회용
  private String message;
  private List<CalendarScheduleList> googleSchedules;
  private List<CalendarScheduleList> internalSchedules;

  // 주간 일정 추가
  private List<WeeklySchedules> weeklySchedules;


  // 그룹 리스트 조회
  @Getter
  @Setter
  public static class GroupList {
    private Long groupId;
    private String groupName; // 그룹 이름
    private String description;
    private MeetingType meetingType; // on,off
    private Integer maxMember;
    private Integer currentMember;
    private LocalDateTime createdAt;
    private Boolean isGroupEvent;
    private Integer profileImageNumber;

  }


  // 일정 조회
  @Getter
  @Setter
  public static class ScheduleList {
    private Long scheduleId;
    private Long groupId;
    private String name; // 일정 이름
    private MeetingType meetingType; // on,off
    private MeetingPlatform meetingPlatform;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ScheduleStatus scheduleStatus; // recommend, fixed, complete
    private String description; // 일정에 대한 상세 설명, 내용
    private String location;
    private Boolean isGrouped;
    private String groupName;

  }


  // 캘린더 조회
  @Getter
  @Setter
  public static class CalendarScheduleList {
    private Long scheduleId;
    private String memberId;
    private Long calendarId;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private Boolean isGrouped;
    private String GroupName;
    private MeetingType meetingType;

  }

  // 주간 일정을 담는 내부 클래스
  @Getter
  @Setter
  public static class WeeklySchedules {
    private Integer weekNumber;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private List<CalendarScheduleList> googleSchedules;
    private List<CalendarScheduleList> internalSchedules;
  }
}

