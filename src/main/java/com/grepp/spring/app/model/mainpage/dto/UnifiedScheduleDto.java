package com.grepp.spring.app.model.mainpage.dto;

import static com.grepp.spring.app.model.mainpage.service.MainPageService.parseDateOrDateTime;

import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.mainpage.code.ScheduleSource;
import com.grepp.spring.app.model.mypage.dto.PublicCalendarEventDto;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnifiedScheduleDto { //  for (구글 일정 + 내부 일정) 하나의 형태로 묶기
  private Long id;                    // 일정 ID (SERVICE면 scheduleId, GOOGLE이면 calendarDetailId)
  private String googleEventId;       // 구글 이벤트 ID
  private String name;                // 일정 이름
  private String description;         // 상세 설명
  private LocalDateTime startTime;    // 시작 시간
  private LocalDateTime endTime;      // 종료 시간
  private boolean allDay;
  private String location;            // 장소
  private String specificLocation;
  private Boolean isGrouped;          // 그룹 일정 여부
  private String groupName;           // 그룹명 (그룹 일정일 경우)
  private String participantNames;
  private MeetingType meetingType;    // ON/OFF
  private MeetingPlatform meetingPlatform;
  private ScheduleStatus scheduleStatus; // recommend, fixed, complete
  private ScheduleSource source;      // 일정 출처 (SERVICE / GOOGLE)
  private Boolean activated;
  private Long scheduleMemberId;


  // 우리 서비스 일정 → DTO
  public static UnifiedScheduleDto fromService(
      Schedule s,
      Group g,
      ScheduleMember sm ,
      List<ScheduleMember> scheduleMembers
  ) {

    String participantNames = scheduleMembers.stream()
        .map(m -> m.getMember().getName()) // Member 엔티티에서 이름 꺼내기
        .collect(Collectors.joining(", "));

    boolean isGrouped = g != null && Boolean.TRUE.equals(g.getIsGrouped());
    String groupName = isGrouped ? g.getName() : null;

    return UnifiedScheduleDto.builder()
        .id(s.getId())
        .googleEventId(null)
        .name(s.getScheduleName())
        .description(s.getDescription())
        .startTime(s.getStartTime()) // 구글 이름이랑 혼동 주의
        .endTime(s.getEndTime())
        .allDay(false) // 내부 일정은 종일 개념 없으니까 false 로 고정
        .location(s.getLocation())
        .specificLocation(s.getSpecificLocation())
        .isGrouped(g.getIsGrouped()) // 필요하면 s.getEvent() != null 로 변경
        .groupName(g.getIsGrouped() ? g.getName() : null)  // 그룹 일정이면 s.getEvent().getGroup().getName()
        .participantNames(participantNames)
        .meetingType(s.getEvent().getMeetingType()) // 아직 MeetingType이 없다면 null 처리
        .meetingPlatform(s.getMeetingPlatform())
        .scheduleStatus(s.getStatus())
        .source(ScheduleSource.SERVICE)
        .activated(sm.getActivated())
        .scheduleMemberId(sm.getId())
        .build();
  }

  // 구글 일정 → DTO
  public static UnifiedScheduleDto fromPublicCalendar(
      PublicCalendarEventDto e,
      LocalDateTime startTime,
      LocalDateTime endTime) {
    return UnifiedScheduleDto.builder()
        .id(null)
        .googleEventId(e.getEventId())
        .name(e.getTitle()) // 구글 일정 제목
        .description(null)
        .startTime(startTime)
        .endTime(endTime)
        .allDay(e.isAllDay())
        .location(null)
        .isGrouped(null) // 구글 일정엔 그룹 개념 없음
        .groupName(null)
        .meetingType(null)
        .meetingPlatform(null)
        .scheduleStatus(null)
        .source(ScheduleSource.GOOGLE)
        .activated(true)
        .build();
  }


}
