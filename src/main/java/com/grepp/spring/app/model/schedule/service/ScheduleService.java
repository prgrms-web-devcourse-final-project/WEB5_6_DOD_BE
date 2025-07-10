package com.grepp.spring.app.model.schedule.service;

import com.grepp.spring.app.controller.api.schedules.payload.response.ShowScheduleResponse;
import com.grepp.spring.app.model.schedule.dto.ShowScheduleDto;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleRepository;
import com.grepp.spring.app.model.schedule.repository.WorkspaceRepository;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduleService {

    @Autowired private ScheduleRepository scheduleRepository;
    @Autowired private ScheduleMemberRepository scheduleMemberRepository;
    @Autowired private WorkspaceRepository workspaceRepository;

    @Transactional
    public ShowScheduleResponse showSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));

        // Lazy init 해결하기 위해서 Transactional 내에서 처리
        Long eventId = schedule.getEvent().getId();

        List<ScheduleMember> scheduleMembers = scheduleMemberRepository.findByScheduleId(scheduleId);
        List<Workspace> workspaces = workspaceRepository.findAllByScheduleId(scheduleId);

        ShowScheduleDto dto = ShowScheduleDto.toDto(eventId, schedule, scheduleMembers, workspaces);


        return ShowScheduleResponse.fromDto(dto);
    }

    public Optional<Schedule> findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }

    public Optional<Schedule> findEventById(Long eventId) {
        return scheduleRepository.findByEventId(eventId);
    }
}
