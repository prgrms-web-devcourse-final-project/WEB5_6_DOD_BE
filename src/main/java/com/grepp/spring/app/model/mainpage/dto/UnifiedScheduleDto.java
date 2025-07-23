package com.grepp.spring.app.model.mainpage.dto;

import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.mainpage.code.ScheduleSource;
import com.grepp.spring.app.model.mainpage.entity.CalendarDetail;
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
  private String name;                // 일정 이름
  private String description;         // 상세 설명
  private LocalDateTime startTime;    // 시작 시간
  private LocalDateTime endTime;      // 종료 시간
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
        .name(s.getScheduleName())
        .description(s.getDescription())
        .startTime(s.getStartTime()) // 구글 이름이랑 혼동 주의
        .endTime(s.getEndTime())
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
  public static UnifiedScheduleDto fromGoogle(CalendarDetail g) {
    return UnifiedScheduleDto.builder()
        .id(g.getId())
        .name(g.getTitle()) // 구글 일정 제목
        .description(null)
        .startTime(g.getStartDatetime())
        .endTime(g.getEndDatetime())
        .location(null)
        .isGrouped(null) // 구글 일정엔 그룹 개념 없음
        .groupName(null)
        .meetingType(null)
        .meetingPlatform(null)
        .scheduleStatus(null)
        .source(ScheduleSource.GOOGLE)
        .build();
  }
}
