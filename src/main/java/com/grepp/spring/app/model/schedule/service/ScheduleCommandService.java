package com.grepp.spring.app.model.schedule.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.spring.app.controller.api.mypage.payload.response.GoogleTokenResponse;
import com.grepp.spring.app.controller.api.schedule.payload.request.AddWorkspaceRequest;
import com.grepp.spring.app.controller.api.schedule.payload.request.CreateDepartLocationRequest;
import com.grepp.spring.app.controller.api.schedule.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.controller.api.schedule.payload.request.ModifySchedulesRequest;
import com.grepp.spring.app.controller.api.schedule.payload.request.WriteSuggestedLocationRequest;
import com.grepp.spring.app.controller.api.schedule.payload.response.CreateOnlineMeetingRoomResponse;
import com.grepp.spring.app.controller.api.schedule.payload.response.CreateSchedulesResponse;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.app.model.schedule.code.VoteStatus;
import com.grepp.spring.app.model.schedule.dto.AddWorkspaceDto;
import com.grepp.spring.app.model.schedule.dto.CreateDepartLocationDto;
import com.grepp.spring.app.model.schedule.dto.CreateOnlineMeetingRoomDto;
import com.grepp.spring.app.model.schedule.dto.CreateScheduleDto;
import com.grepp.spring.app.model.schedule.dto.CreateScheduleMembersDto;
import com.grepp.spring.app.model.schedule.dto.DepartLocationMetroTransferDto;
import com.grepp.spring.app.model.schedule.dto.ModifyScheduleDto;
import com.grepp.spring.app.model.schedule.dto.ModifyWorkspaceDto;
import com.grepp.spring.app.model.schedule.dto.SubwayStationDto;
import com.grepp.spring.app.model.schedule.dto.VoteMiddleLocationDto;
import com.grepp.spring.app.model.schedule.dto.WriteSuggestedLocationDto;
import com.grepp.spring.app.model.schedule.dto.WriteSuggestedMetroTransferDto;
import com.grepp.spring.app.model.schedule.dto.ZoomMeetingDto;
import com.grepp.spring.app.model.schedule.entity.Line;
import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.Metro;
import com.grepp.spring.app.model.schedule.entity.MetroTransfer;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Vote;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import com.grepp.spring.app.model.schedule.repository.LineQueryRepository;
import com.grepp.spring.app.model.schedule.repository.LocationCommandRepository;
import com.grepp.spring.app.model.schedule.repository.LocationQueryRepository;
import com.grepp.spring.app.model.schedule.repository.MetroQueryRepository;
import com.grepp.spring.app.model.schedule.repository.MetroTransferCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleQueryRepository;
import com.grepp.spring.app.model.schedule.repository.VoteCommandRepository;
import com.grepp.spring.app.model.schedule.repository.VoteQueryRepository;
import com.grepp.spring.app.model.schedule.repository.WorkspaceCommandRepository;
import com.grepp.spring.app.model.schedule.repository.WorkspaceQueryRepository;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import com.grepp.spring.infra.error.exceptions.group.GroupNotFoundException;
import com.grepp.spring.infra.error.exceptions.group.UserNotFoundException;
import com.grepp.spring.infra.error.exceptions.schedule.LocationNotFoundException;
import com.grepp.spring.infra.response.GroupErrorCode;
import com.grepp.spring.infra.response.ScheduleErrorCode;
import com.grepp.spring.infra.utils.RandomPicker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleCommandService {

    @PersistenceContext
    private EntityManager em;

    private final ScheduleQueryService scheduleQueryService;


    private final ScheduleQueryRepository scheduleQueryRepository;
    private final ScheduleCommandRepository scheduleCommandRepository;

    private final ScheduleMemberQueryRepository scheduleMemberQueryRepository;

    private final WorkspaceQueryRepository workspaceQueryRepository;
    private final WorkspaceCommandRepository workspaceCommandRepository;

    private final MetroTransferCommandRepository metroTransferCommandRepository;

    private final LineQueryRepository lineQueryRepository;

    private final MemberRepository memberRepository;

    private final VoteQueryRepository voteQueryRepository;

    private final LocationQueryRepository locationQueryRepository;
    private final VoteCommandRepository voteCommandRepository;

    private final LocationCommandRepository locationCommandRepository;

    private final MetroQueryRepository metroQueryRepository;
    private final ScheduleMemberRepository scheduleMemberRepository;

    private final EntityManager entityManager;

    @Value("${kakao.middle-location.api-key}")
    private String kakaoMiddleLocationApiKey;

    @Autowired
    private ZoomOAuthService zoomOAuthService;

    @Value("${zoom.refresh-token}")
    private String zoomRefreshToken;

    // 공통 로직
    private Optional<Schedule> getSchedule(Long scheduleId) {
        Optional<Schedule> schedule = scheduleQueryRepository.findById(scheduleId);
        return schedule;
    }

    private Member memberValid(String memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new UserNotFoundException(GroupErrorCode.USER_NOT_FOUND));
        return member;
    }

    private ScheduleMember getScheduleMember(Long scheduleId, String userId) {
        ScheduleMember scheduleMember = scheduleMemberQueryRepository.findScheduleMember(userId,
            scheduleId);
        return scheduleMember;
    }

    private Optional<Metro> getMetro(String departLocationName) {
        Optional<Metro> metro = metroQueryRepository.findByName(departLocationName);
        return metro;
    }

    private Location getLocation(Location lid) {
        Location location = locationQueryRepository.findById(lid.getId())
            .orElseThrow(() -> new LocationNotFoundException(ScheduleErrorCode.LOCATION_NOT_FOUND));
        return location;
    }

    @Transactional
    public CreateSchedulesResponse createSchedule(CreateSchedulesRequest request, String userId) {
        try {
          Event event = scheduleQueryService.findEventById(request.getEventId());

          event.activation();
          entityManager.flush();
          Schedule schedule = create(request, event);
          createScheduleMembers(request, schedule, userId);

          return CreateScheduleDto.toResponse(schedule.getId());
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new GroupNotFoundException(GroupErrorCode.GROUP_NOT_FOUND);
        }
    }

    private Schedule create(CreateSchedulesRequest request, Event event) {
        CreateScheduleDto dto = CreateScheduleDto.toDto(request);
        Schedule schedule = CreateScheduleDto.fromDto(dto, event);

        scheduleCommandRepository.save(schedule);
        return schedule;
    }

    private void createScheduleMembers(CreateSchedulesRequest request, Schedule schedule,
        String userId) {
        for (CreateScheduleMembersDto entry : request.getMembers()) {
            String memberId = String.valueOf(entry.getMemberId());

            ScheduleRole role;

            if (entry.getMemberId().equals(userId)) {
                role = ScheduleRole.ROLE_MASTER;
            } else {
                role = ScheduleRole.ROLE_MEMBER;
            }

            Member member = memberValid(memberId);

            ScheduleMember scheduleMember = ScheduleMember.builder()
                .name(member.getName())
                .role(role)
                .member(member)
                .schedule(schedule)
                .build();

            scheduleMemberQueryRepository.save(scheduleMember);
        }
    }

    @Transactional
    public void modifySchedule(ModifySchedulesRequest request, Long scheduleId, String userId) {

        ScheduleMember scheduleMember = getScheduleMember(scheduleId, userId);

        scheduleMember.isScheduleMasterOrThrow();

        ModifyScheduleDto dto = ModifyScheduleDto.toDto(request);

        modifyScheduleEntity(scheduleId, dto);

        modifyWorkspaceEntity(scheduleId, dto, request.getWorkspaceId());

    }

    private void modifyScheduleEntity(Long scheduleId, ModifyScheduleDto dto) {
        Optional<Schedule> schedule = getSchedule(scheduleId);
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

        schedule.get().setPlatformUrl(dto.getPlatformURL());
    }

    private void modifyWorkspaceEntity(Long scheduleId, ModifyScheduleDto dto, Long workspaceId) {
        Workspace workspace = workspaceQueryRepository.findworkspace(scheduleId, workspaceId);

        if (dto.getWorkspace() != null && !dto.getWorkspace().isEmpty()) {
            ModifyWorkspaceDto modifyWorkspaceDto = dto.getWorkspace().get(0);

            if (modifyWorkspaceDto.getType() != null) {
                workspace.setType(modifyWorkspaceDto.getType());
            }

            if (modifyWorkspaceDto.getName() != null) {
                workspace.setName(modifyWorkspaceDto.getName());
            }

            if (modifyWorkspaceDto.getUrl() != null) {
                workspace.setUrl(modifyWorkspaceDto.getUrl());
            }
        }
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, String userId) {

        ScheduleMember scheduleMember = getScheduleMember(scheduleId, userId);

        scheduleMember.isScheduleMasterOrThrow();

        scheduleCommandRepository.deleteById(scheduleId);
    }

    public void AddWorkspace(Schedule schedule, AddWorkspaceRequest request) {
        AddWorkspaceDto dto = AddWorkspaceDto.toDto(schedule, request);
        Workspace workspace = AddWorkspaceDto.fromDto(dto);
        workspaceCommandRepository.save(workspace);
    }

    public void deleteWorkspace(Long workspaceId) {
        workspaceCommandRepository.deleteById(workspaceId);
    }

    @Transactional // Transactional 내에서 수정이 되어야 자동 변경 감지된다.
    public void createDepartLocation(Long scheduleId, CreateDepartLocationRequest request,
        String userId) throws JsonProcessingException {

        Optional<Schedule> schedule = getSchedule(scheduleId);

        // 출발장소 추가될때마다 매번 다른 중간장소가 나와야함. 기존의 중간장소는 모두 삭제
        metroTransferCommandRepository.deleteByScheduleId(scheduleId);
        locationCommandRepository.deleteLocation(scheduleId);

        ScheduleMember scheduleMember = getScheduleMember(scheduleId, userId);
        Optional<Metro> metro = getMetro(request.getDepartLocationName());
        setDepartLocation(request, metro, scheduleMember);

        List<ScheduleMember> scheduleLocations = scheduleMemberQueryRepository.findByScheduleId(
            scheduleId);

        // 출발장소들을 이용하여 중간장소 계산
        Double middleLatitude = getLatitude(scheduleLocations);
        Double middleLongitude = getLongitude(scheduleLocations);

        // 3개의 지하철역 정보를 가져오기
        List<JsonNode> subwayStation = findNearestStations(middleLatitude, middleLongitude);

        // 중간 지하철역 후보 저장
        saveMiddleLocation(subwayStation, schedule);

        em.flush();  // DB 반영
        em.clear();  // 영속성 컨텍스트 초기화
    }

    private void saveMiddleLocation(List<JsonNode> subwayStation, Optional<Schedule> schedule) {
        Optional<Metro> metro;
        for (JsonNode subwayStationJson : subwayStation) {
            SubwayStationDto subwayStationDto = SubwayStationDto.toDto(subwayStationJson,
                schedule.get());
            Location location = SubwayStationDto.fromDto(subwayStationDto);
            location = locationCommandRepository.save(location);

            log.info("location = {}", location);

            metro = metroQueryRepository.findByName(location.getName());
            List<Line> line = lineQueryRepository.findByMetroId(metro.get().getId());

            for (Line l : line) {
                DepartLocationMetroTransferDto dto = DepartLocationMetroTransferDto.toDto(location,
                    l);
                MetroTransfer metroTransfer = DepartLocationMetroTransferDto.fromDto(dto);
                metroTransferCommandRepository.save(metroTransfer);
            }
        }
    }

    private static Double getLongitude(List<ScheduleMember> scheduleLocations) {
        Double middleLongitude = 0.0;
        int cnt = 0;
        for (ScheduleMember sc : scheduleLocations) {
            if (sc.getLongitude() != null) {
                cnt++;
                middleLongitude += sc.getLongitude();      // 중간 위도 계산
            }
        }
        middleLongitude = middleLongitude / cnt;
        return middleLongitude;
    }

    private static Double getLatitude(List<ScheduleMember> scheduleLocations) {
        Double middleLatitude = 0.0;
        int cnt = 0;
        for (ScheduleMember sc : scheduleLocations) {
            if (sc.getLatitude() != null) {
                cnt++;
                middleLatitude += sc.getLatitude();        // 중간 경도 계산
            }
        }
        middleLatitude = middleLatitude / cnt;
        return middleLatitude;
    }

    private static void setDepartLocation(CreateDepartLocationRequest request,
        Optional<Metro> metro,
        ScheduleMember scheduleMember) {
        // DB에 존재하지 않는다면
        if (metro.isEmpty()) {
            CreateDepartLocationDto dto = CreateDepartLocationDto.toDto(request);

            scheduleMember.setDepartLocationName(dto.getDepartLocationName());
            scheduleMember.setLongitude(dto.getLongitude());
            scheduleMember.setLatitude(dto.getLatitude());
        } else { // DB에 존재한다면
            CreateDepartLocationDto dto = CreateDepartLocationDto.entityToDto(metro.get());

            scheduleMember.setDepartLocationName(dto.getDepartLocationName());
            scheduleMember.setLongitude(dto.getLongitude());
            scheduleMember.setLatitude(dto.getLatitude());
        }
    }

    // 카카오 api 활용하여 중간장소 역 3개 추출
    private List<JsonNode> findNearestStations(double latitude, double longitude)
        throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl(
                "https://dapi.kakao.com/v2/local/search/category.json")
            .queryParam("category_group_code", "SW8")
            .queryParam("x", longitude) // x = 경도
            .queryParam("y", latitude)  // y = 위도
            .queryParam("radius", 3000)
            .queryParam("sort", "distance")
            .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoMiddleLocationApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity,
            String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.getBody());

        JsonNode documents = json.get("documents");
        List<JsonNode> result = new ArrayList<>();
        String stationName = "";

        // 3개까지만 반환
        // ex) 강남역 2호선 , 강남역 신분당선 -> 아래는 같은역 저장 방지 로직
        for (JsonNode doc : documents) {
            String fullName = doc.get("place_name").asText();
            String sn = fullName.split(" ")[0];

            // 직전에 나왔던 역이 아니라면 저장
            // 거리순으로 역이 차례대로 추출되기 때문에 같은역은 반드시 붙어서 나오게 되므로
            // 직전 역이 같은지만 판단
            if (!stationName.equals(sn)) {
                stationName = sn;
                result.add(doc);
            }

            // 3개역만 저장
            if (result.size() >= 3) {
                break;
            }
        }

        return result;
    }

    @Transactional
    public void voteMiddleLocation(Schedule schedule, ScheduleMember scheduleMember, Location lid) {

        VoteMiddleLocationDto dto = VoteMiddleLocationDto.toDto(scheduleMember, lid, schedule);
        Vote vote = VoteMiddleLocationDto.fromDto(dto);
        voteCommandRepository.save(vote);

        Location location = getLocation(lid);

        location.setVoteCount(location.getVoteCount() + 1);

        List<Location> locationList = locationQueryRepository.findByScheduleId(schedule.getId());
        int scheduleMemberNumber = scheduleMemberQueryRepository.findByScheduleId(schedule.getId())
            .size();
        int voteCount = voteQueryRepository.findByScheduleId(schedule.getId()).size();

        if (scheduleMemberNumber - voteCount == 0) {
            int winner = 0;
            Long winnerLid = null;
            for (Location l : locationList) {
                if (winner <= l.getVoteCount()) {
                    winner = l.getVoteCount();
                    winnerLid = l.getId();
                }
            }

            Optional<Location> winnerLocation = locationQueryRepository.findById(winnerLid);
            winnerLocation.get().setStatus(VoteStatus.WINNER);
        }

    }

    @Transactional
    public CreateOnlineMeetingRoomResponse createOnlineMeeting(Long scheduleId) {

        Schedule schedule = scheduleQueryRepository.findById(scheduleId)
            .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다. (ID: " + scheduleId + ")"));

        GoogleTokenResponse tokenResponse = zoomOAuthService.refreshAccessToken(zoomRefreshToken);
        String accessToken = tokenResponse.getAccessToken();

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalStateException("Zoom 인증 토큰을 갱신하는데 실패했습니다. 리프레시 토큰을 확인하세요.");
        }

        String apiUrl = "https://api.zoom.us/v2/users/me/meetings";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format(
            "{\"topic\":\"%s\", \"type\":2, \"start_time\":\"%s\", \"timezone\":\"Asia/Seoul\"}",
            schedule.getScheduleName(),
            schedule.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
        );

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ZoomMeetingDto> responseEntity = restTemplate.postForEntity(apiUrl,
            requestEntity, ZoomMeetingDto.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
            throw new RuntimeException(
                "Zoom API를 통해 회의를 생성하는데 실패했습니다. 응답: " + responseEntity.getBody());
        }

        String meetingLink = responseEntity.getBody().getJoinUrl();

        schedule.setMeetingPlatform(MeetingPlatform.ZOOM);
        schedule.setPlatformUrl(meetingLink);
        scheduleCommandRepository.save(schedule);

        CreateOnlineMeetingRoomDto dto = CreateOnlineMeetingRoomDto.toDto(meetingLink);
        return CreateOnlineMeetingRoomDto.fromDto(dto);
    }

    @Transactional
    public void WriteSuggestedLocation(Schedule schedule, WriteSuggestedLocationRequest request,
        String userId) {

        List<Location> locationList = locationQueryRepository.findByScheduleId(schedule.getId());
        boolean bool = true;

        for (Location l : locationList) {
            if (l.getVoteCount() != 0) {
                bool = false;
            }
        }

        if (bool) {
            ScheduleMember scheduleMember = getScheduleMember(schedule.getId(), userId);
            Member member = memberRepository.findById(userId).orElseThrow();
            Optional<Metro> metro = getMetro(request.getLocationName());

            scheduleMember.isScheduleMasterOrThrow();

            Location location = saveSuggestedLocation(schedule, request, metro, member);

            metro = metroQueryRepository.findByName(location.getName());
            List<Line> line = lineQueryRepository.findByMetroId(metro.get().getId());

            for (Line l : line) {
                WriteSuggestedMetroTransferDto dto = WriteSuggestedMetroTransferDto.toDto(schedule,
                    location, l);
                MetroTransfer metroTransfer = WriteSuggestedMetroTransferDto.fromDto(dto);
                metroTransferCommandRepository.save(metroTransfer);
            }
        } else {
            throw new RuntimeException("투표중입니다! 투표중에는 후보장소를 등록할 수 없습니다.");
        }

    }

    private Location saveSuggestedLocation(Schedule schedule, WriteSuggestedLocationRequest request,
        Optional<Metro> metro, Member member) {
        Location location;
        // DB에 존재하지 않는다면
        if (metro.isEmpty()) {
            WriteSuggestedLocationDto dto = WriteSuggestedLocationDto.requestToDto(request,
                schedule, member);

            location = WriteSuggestedLocationDto.fromDto(dto);
            location = locationCommandRepository.save(location);
        } else { // DB에 존재한다면
            location = WriteSuggestedLocationDto.metroToEntity(metro.get(), schedule, member);
            location = locationCommandRepository.save(location);
        }
        return location;
    }

    // 회원 탈퇴 중 일정 관련 처리 메서드
    @Transactional
    public void handleScheduleWithdrawal(Member member) {
        // 본인이 일정 마스터인 모든 일정 조회
        List<Schedule> masterSchedules = scheduleMemberRepository.findByMember(member);

        // 본인이 마스터인 일정이 있다면,
        if (!masterSchedules.isEmpty()) {
            for (Schedule schedule : masterSchedules) {
                // 각 일정 내 모든 멤바 조회 (나 빼고)
                List<ScheduleMember> scheduleMembers = scheduleMemberRepository.findByScheduleAndMemberNot(
                    schedule, member);

                if (scheduleMembers.isEmpty()) {
                    // 본인이 일정의 유일 멤버라면? 일정 너도 삭제야.
                    scheduleCommandRepository.delete(schedule);
                    log.info("일정 {}의 마지막 멤버이므로 일정이 삭제됩니다.", schedule.getScheduleName());
                } else {
                    // 다른 멤바가 있다면 랜덤으로 관리자 위임
                    ScheduleMember newScheduleMaster = RandomPicker.pickRandom(scheduleMembers);
                    newScheduleMaster.grantMasterRole(); // 새 관리자로 임명
                    scheduleMemberRepository.save(newScheduleMaster);
                    log.info("일정 {}의 새 관리자가 {}님 에게 위임되었습니다.", schedule.getScheduleName(),
                        newScheduleMaster.getMember().getName());
                }
            }
        }
    }
}
