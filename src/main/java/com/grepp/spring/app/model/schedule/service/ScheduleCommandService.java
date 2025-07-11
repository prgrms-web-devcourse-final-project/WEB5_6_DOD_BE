package com.grepp.spring.app.model.schedule.service;

import com.grepp.spring.app.controller.api.schedules.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.app.model.schedule.dto.CreateScheduleDto;
import com.grepp.spring.app.model.schedule.dto.ScheduleMemberRolesDto;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
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

    @Autowired private ScheduleCommandRepository scheduleCommandRepository;
    @Autowired private ScheduleMemberQueryRepository scheduleMemberQueryRepository;
    @Autowired private WorkspaceQueryRepository workspaceQueryRepository;

    @Autowired private EventRepository eventRepository;

    @Autowired private MemberRepository memberRepository;


//    @Transactional
    public ShowScheduleResponse showSchedule(Long scheduleId) {
        Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);

        // Lazy init 해결하기 위해서 Transactional 내에서 처리
        Long eventId = schedule.get().getEvent().getId();

        List<ScheduleMember> scheduleMembers = scheduleMemberRepository.findByScheduleId(scheduleId);
        List<Workspace> workspaces = workspaceRepository.findAllByScheduleId(scheduleId);

        ShowScheduleDto dto = ShowScheduleDto.fromEntity(eventId, schedule.orElse(null), scheduleMembers, workspaces);


        return ShowScheduleDto.fromDto(dto);
    }

    public Optional<Schedule> findScheduleById(Long scheduleId) {
        return scheduleQueryRepository.findById(scheduleId);
    }

    @Transactional
    public void createSchedule(CreateSchedulesRequest request) {
        Optional<Event> eid = eventRepository.findById(request.getEventId());

        CreateScheduleDto dto = CreateScheduleDto.toDto(request);

        Schedule schedule = CreateScheduleDto.fromDto(dto, eid.orElse(null));

        scheduleCommandRepository.save(schedule);

        for (ScheduleMemberRolesDto entry : request.getMemberRoles()) {
            String memberId = String.valueOf(entry.getMemberId());
            ScheduleRole role = entry.getRole();

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
        scheduleCommandRepository.deleteById(scheduleId);
    }

}
