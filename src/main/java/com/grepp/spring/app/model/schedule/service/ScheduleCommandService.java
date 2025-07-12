package com.grepp.spring.app.model.schedule.service;

import com.grepp.spring.app.controller.api.schedules.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.controller.api.schedules.payload.request.ModifySchedulesRequest;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.app.model.schedule.dto.CreateScheduleDto;
import com.grepp.spring.app.model.schedule.dto.ModifyScheduleDto;
import com.grepp.spring.app.model.schedule.dto.ScheduleMemberRolesDto;
import com.grepp.spring.app.model.schedule.dto.WorkspaceDto;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.app.model.schedule.repository.WorkspaceQueryRepository;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
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
    @Autowired private ScheduleQueryRepository scheduleQueryRepository;

    @Autowired private EventRepository eventRepository;

    @Autowired private MemberRepository memberRepository;
    @Autowired
    private ScheduleMemberCommandRepository scheduleMemberCommandRepository;


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

    @Transactional // JPA 영속성 컨텍스트 변경 감지. setter를 사용해서 값 바꾸면 자동으로 변경
    public void modifySchedule(ModifySchedulesRequest request, Long scheduleId) {

        ModifyScheduleDto dto = ModifyScheduleDto.toDto(request);

        modifyScheduleEntity(scheduleId, dto);

        modifyWorkspaceEntity(scheduleId, dto, request.getWorkspaceId());
    }

    private void modifyScheduleEntity(Long scheduleId, ModifyScheduleDto dto) {
        Optional<Schedule> schedule = scheduleQueryRepository.findById(scheduleId);
        if (dto.getStartTime() != null) {
            schedule.get().setStartTime(dto.getStartTime());
        }

        if (dto.getEndTime() != null) {
            schedule.get().setEndTime(dto.getEndTime());
        }

        if (dto.getStatus() != null) {
            schedule.get().setStatus(dto.getStatus());
        }

        if (dto.getScheduleName() != null) {
            schedule.get().setScheduleName(dto.getScheduleName());
        }

        if (dto.getDescription() != null) {
            schedule.get().setDescription(dto.getDescription());
        }

        if (dto.getLocation() != null) {
            schedule.get().setLocation(dto.getLocation());
        }

        if (dto.getSpecificLocation() != null) {
            schedule.get().setSpecificLocation(dto.getSpecificLocation());
        }

        if (dto.getMeetingPlatform() != null) {
            schedule.get().setMeetingPlatform(dto.getMeetingPlatform());
        }

        if (dto.getPlatformName() != null) {
            schedule.get().setPlatformName(dto.getPlatformName());
        }

        if (dto.getPlatformURL() != null) {
            schedule.get().setPlatformUrl(dto.getPlatformURL());
        }
    }

    private void modifyWorkspaceEntity(Long scheduleId, ModifyScheduleDto dto, Long workspaceId) {
        Workspace workspace = workspaceQueryRepository.findworkspace(scheduleId, workspaceId);

        if (dto.getWorkspaces() != null && !dto.getWorkspaces().isEmpty()) {
            WorkspaceDto workspaceDto = dto.getWorkspaces().get(0);

            if (workspaceDto.getType() != null) {
                workspace.setType(workspaceDto.getType());
            }

            if (workspaceDto.getName() != null) {
                workspace.setName(workspaceDto.getName());
            }

            if (workspaceDto.getUrl() != null) {
                workspace.setUrl(workspaceDto.getUrl());
            }
        }
    }


    @Transactional
    public void deleteSchedule(Long scheduleId) {

        scheduleMemberCommandRepository.deleteAllByScheduleId(scheduleId);
        scheduleCommandRepository.deleteById(scheduleId);
    }

}
