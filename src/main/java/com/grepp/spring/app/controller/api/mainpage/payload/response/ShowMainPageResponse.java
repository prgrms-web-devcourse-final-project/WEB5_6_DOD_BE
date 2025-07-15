package com.grepp.spring.app.controller.api.mainpage.payload.response;


import com.grepp.spring.app.controller.api.group.payload.response.ShowGroupResponse;
import com.grepp.spring.app.controller.api.schedules.payload.response.ShowScheduleResponse;
import com.grepp.spring.app.model.mainpage.dto.UnifiedScheduleDto;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowMainPageResponse {

  private ShowGroupResponse groups;           // 속한 그룹 정보
  private List<UnifiedScheduleDto> schedules; // 오늘/선택 날짜 일정 (구글+내부 통합)
  private List<WeeklyScheduleDto> weeklySchedules; // 주간 통합 일정 (google + service)



  // 주간 일정을 담는 내부 클래스
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class WeeklyScheduleDto {
    private Integer weekNumber;         // 몇 번째 주 (해당 연도의 몇번 째 주)
    private LocalDate weekStartDate;    // 주 시작일
    private LocalDate weekEndDate;      // 주 종료일
    private List<UnifiedScheduleDto> schedules; // 해당 주의 통합 일정 (google + service)
  }
}

