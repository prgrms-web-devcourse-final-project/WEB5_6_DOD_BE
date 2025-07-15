package com.grepp.spring.app.model.schedule.service;

import com.grepp.spring.app.controller.api.schedules.payload.response.ShowScheduleResponse;
import com.grepp.spring.app.controller.api.schedules.payload.response.ShowSuggestedLocationsResponse;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.dto.MetroInfoDto;
import com.grepp.spring.app.model.schedule.dto.MetroTransferDto;
import com.grepp.spring.app.model.schedule.dto.ShowScheduleDto;
import com.grepp.spring.app.model.schedule.dto.ShowSuggestedLocationsDto;
import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.MetroTransfer;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import com.grepp.spring.app.model.schedule.repository.LocationQueryRepository;
import com.grepp.spring.app.model.schedule.repository.MetroTransferQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.app.model.schedule.repository.VoteQueryRepository;
import com.grepp.spring.app.model.schedule.repository.WorkspaceQueryRepository;
import com.grepp.spring.infra.error.exceptions.group.ScheduleNotFoundException;
import com.grepp.spring.infra.response.GroupErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ScheduleQueryService {

    @Autowired
    private ScheduleQueryRepository scheduleQueryRepository;
    @Autowired
    private ScheduleMemberQueryRepository scheduleMemberQueryRepository;
    @Autowired
    private WorkspaceQueryRepository workspaceQueryRepository;

    @Autowired private EventRepository eventRepository;

    @Autowired private MemberRepository memberRepository;

    @Autowired private LocationQueryRepository locationQueryRepository;
    @Autowired
    private VoteQueryRepository voteQueryRepository;
    @Autowired
    private MetroTransferQueryRepository metroTransferQueryRepository;


    @Transactional
    public ShowScheduleResponse showSchedule(Long scheduleId) {
        Optional<Schedule> schedule = scheduleQueryRepository.findById(scheduleId);

        // Lazy init 해결하기 위해서 Transactional 내에서 처리
        Long eventId = schedule.get().getEvent().getId();

        List<ScheduleMember> scheduleMembers = scheduleMemberQueryRepository.findByScheduleId(
            scheduleId);
        List<Workspace> workspaces = workspaceQueryRepository.findAllByScheduleId(scheduleId);

        ShowScheduleDto dto = ShowScheduleDto.fromEntity(eventId, schedule.orElse(null),
            scheduleMembers, workspaces);

        return ShowScheduleDto.fromDto(dto);
    }

    public Schedule findScheduleById(Long scheduleId) {
        return scheduleQueryRepository.findById(scheduleId).orElseThrow(() -> new ScheduleNotFoundException(
            GroupErrorCode.SCHEDULE_NOT_FOUND));
    }

    public Optional<Event> findEventById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    public ShowSuggestedLocationsResponse showSuggestedLocation(Long scheduleId) {

        List<Location> location = locationQueryRepository.findByScheduleId(scheduleId);

        int scheduleMemberNumber = scheduleMemberQueryRepository.findByScheduleId(scheduleId).size();
        int voteCount = voteQueryRepository.findByScheduleId(scheduleId).size();

        List<MetroInfoDto> infoDto = new ArrayList<>();
            for (Location l : location) {
                List<MetroTransfer> transferForLocation = metroTransferQueryRepository.findByLocationId(l.getId());
                infoDto.add(MetroInfoDto.toDto(l, transferForLocation));
            }

        ShowSuggestedLocationsDto finalDto = ShowSuggestedLocationsDto.fromMetroInfoDto(infoDto, scheduleMemberNumber,voteCount);
        
        return ShowSuggestedLocationsDto.fromDto(finalDto);
    }

}
