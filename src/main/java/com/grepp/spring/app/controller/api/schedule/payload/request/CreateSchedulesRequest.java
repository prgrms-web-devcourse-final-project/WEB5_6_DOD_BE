package com.grepp.spring.app.controller.api.schedule.payload.request;

import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.dto.CreateScheduleMembersDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateSchedulesRequest {
    private Long eventId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Size(max = 20, message = "일정 제목은 20자를 초과할 수 없습니다.")
    private String scheduleName;

    @Size(max = 50, message = "일정 설명은 50자를 초과할 수 없습니다.")
    private String description;

    private ScheduleStatus schedulesStatus;
    private MeetingType meetingType;

    private List<CreateScheduleMembersDto> members;
}
