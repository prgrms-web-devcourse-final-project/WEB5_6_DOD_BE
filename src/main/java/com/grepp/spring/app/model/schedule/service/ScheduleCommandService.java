package com.grepp.spring.app.model.schedule.service;

import com.grepp.spring.app.controller.api.schedules.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.dto.CreateScheduleDto;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.app.model.schedule.repository.WorkspaceQueryRepository;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ScheduleCommandService {

    @Autowired private ScheduleQueryRepository scheduleQueryRepository;
    @Autowired private ScheduleMemberQueryRepository scheduleMemberQueryRepository;
    @Autowired private WorkspaceQueryRepository workspaceQueryRepository;

    @Autowired private EventRepository eventRepository;

    @Autowired private MemberRepository memberRepository;

    public Optional<Schedule> findScheduleById(Long scheduleId) {
        return scheduleQueryRepository.findById(scheduleId);
    }

    public Optional<Event> findEventById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    @Transactional
    public void createSchedule(CreateSchedulesRequest request) {
        Event eid = eventRepository.findById(request.getEventId()).orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

        log.info("eid={}", eid);
        CreateScheduleDto dto = CreateScheduleDto.toDto(request);

        Schedule schedule = Schedule.builder()
            .event(eid)
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .status(dto.getScheduleStatus())
            .scheduleName(dto.getScheduleName())
            .description(dto.getDescription()).build();
        log.info("schedule={}", schedule);

        scheduleQueryRepository.save(schedule);

        for (Map.Entry<String, String> entry : request.getMemberRoles().entrySet()) {
            String memberId = String.valueOf(entry.getKey());
            String role = entry.getValue();

            Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("멤버를 찾을 수 없습니다."));

            ScheduleMember scheduleMember = ScheduleMember.builder()
                .name(member.getName())
                .role(role)
                .member(member)
                .schedule(schedule)
                .build();

            scheduleMemberQueryRepository.save(scheduleMember);
        }
    }

    public void modifySchedule(Long scheduleId) {

    }

    public void deleteSchedule(Long scheduleId) {

        scheduleMemberQueryRepository.deleteById(scheduleId);
        scheduleQueryRepository.deleteById(scheduleId);
    }

}
