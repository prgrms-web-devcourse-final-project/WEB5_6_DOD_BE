package com.grepp.spring.app.model.mainpage.dto;

import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.mainpage.code.ScheduleSource;
import com.grepp.spring.app.model.mainpage.entity.CalendarDetail;
import com.grepp.spring.app.model.mypage.dto.PublicCalendarEventDto;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
//private String groupMemberName;
  private String participantNames;
  private MeetingType meetingType;    // ON/OFF
  private MeetingPlatform meetingPlatform;
  private ScheduleStatus scheduleStatus; // recommend, fixed, complete
  private ScheduleSource source;      // 일정 출처 (SERVICE / GOOGLE)
  private Boolean activated;


  // 우리 서비스 일정 → DTO
  public static UnifiedScheduleDto fromService(
      Schedule s,
      Group g,
      ScheduleMember sm ,
//    List<GroupMember> groupMembers,
      List<ScheduleMember> scheduleMembers
  ) {

//    String memberNames = groupMembers.stream()
//        .map(member -> member.getMember().getName())
//        .collect(Collectors.joining(", "));

    String participantNames = scheduleMembers.stream()
        .map(m -> m.getMember().getName()) // Member 엔티티에서 이름 꺼내기
        .collect(Collectors.joining(", "));

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
//      .groupMemberName(memberNames)
        .participantNames(participantNames)
        .meetingType(s.getEvent().getMeetingType()) // 아직 MeetingType이 없다면 null 처리
        .meetingPlatform(s.getMeetingPlatform())
        .scheduleStatus(s.getStatus())
        .source(ScheduleSource.SERVICE)
        .activated(sm.getActivated())
        .build();
  }

  // 구글 일정 → DTO
  public static UnifiedScheduleDto fromPublicCalendar(PublicCalendarEventDto e) {
    return UnifiedScheduleDto.builder()
        .id(null)
        .googleEventId(e.getEventId())
        .name(e.getTitle()) // 구글 일정 제목
        .description(null)
        .startTime(parseDateOrDateTime(e.getStart()))
        .endTime(parseDateOrDateTime(e.getEnd()))
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

  public static LocalDateTime parseDateOrDateTime(String dateOrDateTime) {
    if (dateOrDateTime == null) return null;
    return (dateOrDateTime.length() == 10) // -> 종일 일정 포맷 길이가 10 (yyyy-mm-dd)
        ? LocalDate.parse(dateOrDateTime).atStartOfDay() // 23일 종일 일정 잡으면 23-24일로 뜸. 시작일(+시간)로만 설정
        : LocalDateTime.parse(dateOrDateTime, DateTimeFormatter.ISO_DATE_TIME);
  }
}
