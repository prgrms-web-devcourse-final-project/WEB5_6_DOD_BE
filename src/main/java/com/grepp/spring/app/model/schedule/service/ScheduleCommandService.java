package com.grepp.spring.app.model.schedule.service;

import com.grepp.spring.app.controller.api.schedules.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.controller.api.schedules.payload.request.AddWorkspaceRequest;
import com.grepp.spring.app.controller.api.schedules.payload.response.ShowScheduleResponse;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.app.model.schedule.dto.AddWorkspaceDto;
import com.grepp.spring.app.model.schedule.dto.CreateScheduleDto;
import com.grepp.spring.app.model.schedule.dto.ScheduleMemberRolesDto;
import com.grepp.spring.app.model.schedule.dto.ShowScheduleDto;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.app.model.schedule.repository.WorkspaceCommandRepository;
import com.grepp.spring.app.model.schedule.repository.WorkspaceQueryRepository;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ScheduleCommandService {

    @Autowired private ScheduleCommandRepository scheduleCommandRepository;
    @Autowired private ScheduleQueryRepository scheduleQueryRepository;
    @Autowired private ScheduleMemberQueryRepository scheduleMemberQueryRepository;

    @Autowired private WorkspaceQueryRepository workspaceQueryRepository;
    @Autowired private WorkspaceCommandRepository workspaceCommandRepository;

    @Autowired private EventRepository eventRepository;

    @Autowired private MemberRepository memberRepository;
    @Autowired
    private ScheduleMemberCommandRepository scheduleMemberCommandRepository;


//    @Transactional
    public ShowScheduleResponse showSchedule(Long scheduleId) {
        Optional<Schedule> schedule = scheduleQueryRepository.findById(scheduleId);

        // Lazy init 해결하기 위해서 Transactional 내에서 처리
        Long eventId = schedule.get().getEvent().getId();

        List<ScheduleMember> scheduleMembers = scheduleMemberQueryRepository.findByScheduleId(scheduleId);
        List<Workspace> workspaces = workspaceQueryRepository.findAllByScheduleId(scheduleId);

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

    @Transactional
    public void deleteSchedule(Long scheduleId) {

        scheduleMemberCommandRepository.deleteAllByScheduleId(scheduleId);
        scheduleCommandRepository.deleteById(scheduleId);
    }

    public void AddWorkspace(Optional<Schedule> scheduleId, AddWorkspaceRequest request) {
        AddWorkspaceDto dto = AddWorkspaceDto.toDto(scheduleId, request);
        Workspace workspace = AddWorkspaceDto.fromDto(dto);
        workspaceCommandRepository.save(workspace);
    }
}
