package com.grepp.spring.app.model.schedule.service;

import com.grepp.spring.app.controller.api.schedules.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.controller.api.schedules.payload.response.ShowScheduleResponse;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.dto.CreateScheduleDto;
import com.grepp.spring.app.model.schedule.dto.ShowScheduleDto;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import com.grepp.spring.app.model.schedule.repository.EventRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleRepository;
import com.grepp.spring.app.model.schedule.repository.WorkspaceRepository;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ScheduleService {

    @Autowired private ScheduleRepository scheduleRepository;
    @Autowired private ScheduleMemberRepository scheduleMemberRepository;
    @Autowired private WorkspaceRepository workspaceRepository;

    // TODO : 추후 Event 패키지의 EventRepo로 변경해야 함. 현재는 Schedule 패키지의 EventRepo임
    @Autowired private EventRepository eventRepository;

    @Autowired private MemberRepository memberRepository;


    @Transactional
    public ShowScheduleResponse showSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));

        // Lazy init 해결하기 위해서 Transactional 내에서 처리
        Long eventId = schedule.getEvent().getId();

        List<ScheduleMember> scheduleMembers = scheduleMemberRepository.findByScheduleId(scheduleId);
        List<Workspace> workspaces = workspaceRepository.findAllByScheduleId(scheduleId);

        ShowScheduleDto dto = ShowScheduleDto.toDto(eventId, schedule, scheduleMembers, workspaces);


        return ShowScheduleDto.fromDto(dto);
    }

    public Optional<Schedule> findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId);
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

        scheduleRepository.save(schedule);

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

            scheduleMemberRepository.save(scheduleMember);
        }
    }

    public void modifySchedule(Long scheduleId) {

    }

    public void deleteSchedule(Long scheduleId) {

        scheduleRepository.deleteById(scheduleId);
    }

}
