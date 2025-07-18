package com.grepp.spring.app.model.schedule.service;

import com.grepp.spring.app.controller.api.schedules.payload.request.CreateDepartLocationRequest;
import com.grepp.spring.app.controller.api.schedules.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.controller.api.schedules.payload.request.AddWorkspaceRequest;
import com.grepp.spring.app.controller.api.schedules.payload.request.ModifySchedulesRequest;
import com.grepp.spring.app.controller.api.schedules.payload.response.CreateOnlineMeetingRoomResponse;
import com.grepp.spring.app.controller.api.schedules.payload.response.CreateSchedulesResponse;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.app.model.schedule.code.VoteStatus;
import com.grepp.spring.app.model.schedule.dto.AddWorkspaceDto;
import com.grepp.spring.app.model.schedule.dto.CreateDepartLocationDto;
import com.grepp.spring.app.model.schedule.dto.CreateOnlineMeetingRoomDto;
import com.grepp.spring.app.model.schedule.dto.CreateScheduleDto;
import com.grepp.spring.app.model.schedule.dto.ModifyScheduleDto;
import com.grepp.spring.app.model.schedule.dto.ScheduleMemberRolesDto;
import com.grepp.spring.app.model.schedule.dto.VoteMiddleLocationDto;
import com.grepp.spring.app.model.schedule.dto.WorkspaceDto;
import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Vote;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import com.grepp.spring.app.model.schedule.repository.LocationCommandRepository;
import com.grepp.spring.app.model.schedule.repository.LocationQueryRepository;
import com.grepp.spring.app.model.schedule.repository.MetroTransferCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.app.model.schedule.repository.VoteQueryRepository;
import com.grepp.spring.app.model.schedule.repository.WorkspaceCommandRepository;
import com.grepp.spring.app.model.schedule.repository.VoteCommandRepository;
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
    @Autowired private ScheduleMemberCommandRepository scheduleMemberCommandRepository;

    @Autowired private WorkspaceQueryRepository workspaceQueryRepository;
    @Autowired private WorkspaceCommandRepository workspaceCommandRepository;

    @Autowired private MetroTransferCommandRepository metroTransferCommandRepository;

    @Autowired private EventRepository eventRepository;

    @Autowired private MemberRepository memberRepository;

    @Autowired
    private VoteQueryRepository voteQueryRepository;


    @Autowired
    private LocationQueryRepository locationQueryRepository;
    @Autowired private VoteCommandRepository voteCommandRepository;
    @Autowired
    private LocationCommandRepository locationCommandRepository;

////    @Transactional
//    public ShowScheduleResponse showSchedule(Long scheduleId) {
//        Optional<Schedule> schedule = scheduleQueryRepository.findById(scheduleId);
//
//        // Lazy init 해결하기 위해서 Transactional 내에서 처리
//        Long eventId = schedule.get().getEvent().getId();
//
//        List<ScheduleMember> scheduleMembers = scheduleMemberQueryRepository.findByScheduleId(scheduleId);
//        List<Workspace> workspaces = workspaceQueryRepository.findAllByScheduleId(scheduleId);
//
//        ShowScheduleDto dto = ShowScheduleDto.fromEntity(eventId, schedule.orElse(null), scheduleMembers, workspaces);
//
//
//        return ShowScheduleDto.fromDto(dto);
//    }

    public Optional<Schedule> findScheduleById(Long scheduleId) {
        return scheduleQueryRepository.findById(scheduleId);
    }

    @Transactional
    public CreateSchedulesResponse createSchedule(CreateSchedulesRequest request) {
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

        return CreateScheduleDto.toResponse(schedule.getId());
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

        if (dto.getSpecificLatitude() != null) {
            schedule.get().setSpecificLatitude(dto.getSpecificLatitude());
        }

        if (dto.getSpecificLongitude() != null) {
            schedule.get().setSpecificLongitude(dto.getSpecificLongitude());
        }

        if (dto.getMeetingPlatform() != null) {
            schedule.get().setMeetingPlatform(dto.getMeetingPlatform());
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

        // workspace, metroTransfer, vote, location, scheduleMember, schedule 순서로 삭제

//        workspaceCommandRepository.deleteByScheduleId(scheduleId); // 워크스페이스 삭제x
//        metroTransferCommandRepository.deleteByScheduleId(scheduleId); // 환승정보 삭제x
//        voteCommandRepository.deleteByScheduleId(scheduleId); // 투표정보 삭제x
//        locationCommandRepository.deleteByScheduleId(scheduleId); // 장소정보 삭제x
//        scheduleMemberCommandRepository.deleteAllByScheduleId(scheduleId); // 스케줄멤버 삭제x
        scheduleCommandRepository.deleteById(scheduleId); // 스케줄 삭제
    }

    public void AddWorkspace(Schedule scheduleId, AddWorkspaceRequest request) {
        AddWorkspaceDto dto = AddWorkspaceDto.toDto(scheduleId, request);
        Workspace workspace = AddWorkspaceDto.fromDto(dto);
        workspaceCommandRepository.save(workspace);
    }
    public void deleteWorkspace(Long workspaceId) {
        workspaceCommandRepository.deleteById(workspaceId);
    }

    @Transactional // Transactional 내에서 수정이 되어야 자동 변경 감지된다.
    public void createDepartLocation(Long scheduleId, CreateDepartLocationRequest request) {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String memberId = authentication.getName();
//        ScheduleMember scheduleMember = scheduleMemberQueryRepository.findByMemberId(memberId, scheduleId);
//
        //임시
        ScheduleMember scheduleMember = scheduleMemberQueryRepository.findScheduleMember(
            request.getMemberId(), scheduleId);

//        Optional<Schedule> schedule = scheduleQueryRepository.findById(scheduleId);

//        CreateDepartLocationDto dto = CreateDepartLocationDto.toDto(request, schedule.get(), request.getMemberId());
        CreateDepartLocationDto dto = CreateDepartLocationDto.toDto(request);

        scheduleMember.setDepartLocationName(dto.getDepartLocationName());
        scheduleMember.setLongitude(dto.getLongitude());
        scheduleMember.setLatitude(dto.getLatitude());

        //TODO : 출발장소들을 이용하여 중간장소 계산

//        saveLocation(dto);

    }

//    private void saveLocation(CreateDepartLocationDto dto) {
//        Location location = Location.builder()
//            .latitude(dto.getLatitude())
//            .longitude(dto.getLongitude())
//            .name(dto.getDepartLocationName())
//            .suggestedMemberId(dto.getSuggestedMemberId())
//            .status(dto.getStatus())
//            .schedule(dto.getScheduleId())
//            .build();
//
//        locationCommandRepository.save(location);
//    }

    @Transactional
    public void voteMiddleLocation( Optional<ScheduleMember> smId , Optional<Location> lid, Schedule schedule) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String memberId = authentication.getName();

        VoteMiddleLocationDto dto = VoteMiddleLocationDto.toDto(smId, lid, schedule);
        Vote vote = VoteMiddleLocationDto.fromDto(dto);
        voteCommandRepository.save(vote);

        Location location = locationQueryRepository
            .findById(lid.map(Location::getId)
                .orElseThrow(() -> new IllegalArgumentException("Location ID 없음")))
            .orElseThrow(() -> new IllegalArgumentException("해당 Location 없음"));

//        if (location.getVoteCount() != null) {
            location.setVoteCount(location.getVoteCount() + 1);
//        } else if (location.getVoteCount() == null) {
//            location.setVoteCount(1);
//        }

        List<Location> location2 = locationQueryRepository.findByScheduleId(schedule.getId());
        int scheduleMemberNumber = scheduleMemberQueryRepository.findByScheduleId(schedule.getId()).size();
        int voteCount = voteQueryRepository.findByScheduleId(schedule.getId()).size();

        if (scheduleMemberNumber - voteCount == 0) {
            int winner = 0;
            Long winnerLid = null;
            for (Location l : location2) {
                if (winner <= l.getVoteCount()) {
                    winner = l.getVoteCount();
                    winnerLid = l.getId();
                }
            }

            Optional<Location> winnerLocation = locationQueryRepository.findById(winnerLid);
            winnerLocation.get().setStatus(VoteStatus.WINNER);
        }

    }

    public CreateOnlineMeetingRoomResponse createOnlineMeeting(Long scheduleId) {

        // TODO : 온라인 플렛폼 생성 로직 + DB에 플렛폼 url 저장

        Optional<Schedule> schedule = scheduleQueryRepository.findById(scheduleId);

        CreateOnlineMeetingRoomDto dto = CreateOnlineMeetingRoomDto.toDto(schedule.get().getPlatformUrl());

        return CreateOnlineMeetingRoomDto.fromDto(dto);
    }
}
