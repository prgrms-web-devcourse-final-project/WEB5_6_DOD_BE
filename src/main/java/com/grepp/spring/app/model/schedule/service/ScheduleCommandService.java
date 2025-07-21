package com.grepp.spring.app.model.schedule.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.spring.app.controller.api.schedule.payload.request.CreateDepartLocationRequest;
import com.grepp.spring.app.controller.api.schedule.payload.request.CreateSchedulesRequest;
import com.grepp.spring.app.controller.api.schedule.payload.request.AddWorkspaceRequest;
import com.grepp.spring.app.controller.api.schedule.payload.request.ModifySchedulesRequest;
import com.grepp.spring.app.controller.api.schedule.payload.request.WriteSuggestedLocationRequest;
import com.grepp.spring.app.controller.api.schedule.payload.response.CreateOnlineMeetingRoomResponse;
import com.grepp.spring.app.controller.api.schedule.payload.response.CreateSchedulesResponse;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.controller.api.mypage.payload.response.GoogleTokenResponse;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.app.model.schedule.code.VoteStatus;
import com.grepp.spring.app.model.schedule.dto.*;
import com.grepp.spring.app.model.schedule.entity.*;
import com.grepp.spring.app.model.schedule.repository.*;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ScheduleCommandService {

    @PersistenceContext
    private EntityManager em;

    @Autowired private ScheduleCommandRepository scheduleCommandRepository;
    @Autowired private ScheduleQueryRepository scheduleQueryRepository;

    @Autowired private ScheduleMemberQueryRepository scheduleMemberQueryRepository;
    @Autowired private ScheduleMemberCommandRepository scheduleMemberCommandRepository;

    @Autowired private WorkspaceQueryRepository workspaceQueryRepository;
    @Autowired private WorkspaceCommandRepository workspaceCommandRepository;

    @Autowired private MetroTransferCommandRepository metroTransferCommandRepository;

    @Autowired private LineQueryRepository lineQueryRepository;

    @Autowired private EventRepository eventRepository;

    @Autowired private MemberRepository memberRepository;

    @Autowired
    private VoteQueryRepository voteQueryRepository;


    @Autowired
    private LocationQueryRepository locationQueryRepository;
    @Autowired private VoteCommandRepository voteCommandRepository;
    @Autowired
    private LocationCommandRepository locationCommandRepository;
    @Autowired
    private MetroQueryRepository metroQueryRepository;

    @Value("${kakao.middle-location.api-key}")
    private String kakaoMiddleLocationApiKey;

    @Autowired private ZoomOAuthService zoomOAuthService;

    @Value("${zoom.refresh-token}") 
    private String zoomRefreshToken;

    /// /    @Transactional
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
    public void createDepartLocation(Long scheduleId, CreateDepartLocationRequest request)
        throws JsonProcessingException {

        Optional<Schedule> schedule = scheduleQueryRepository.findById(scheduleId);

        // 출발장소 추가될때마다 매번 다른 중간장소가 나와야함. 기존의 중간장소는 모두 삭제
        locationCommandRepository.deleteLocation(scheduleId);
//        metroTransferCommandRepository.deleteByScheduleId(scheduleId);

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String memberId = authentication.getName();
//        ScheduleMember scheduleMember = scheduleMemberQueryRepository.findByMemberId(memberId, scheduleId);
//
        //임시
        ScheduleMember scheduleMember = scheduleMemberQueryRepository.findScheduleMember(
            request.getMemberId(), scheduleId);

//        Optional<Schedule> schedule = scheduleQueryRepository.findById(scheduleId);

//        CreateDepartLocationDto dto = CreateDepartLocationDto.toDto(request, schedule.get(), request.getMemberId());

        Optional<Metro> metro = metroQueryRepository.findByName(request.getDepartLocationName());

        // DB에 존재하지 않는다면
        if (metro.isEmpty()) {
            CreateDepartLocationDto dto = CreateDepartLocationDto.toDto(request);

            scheduleMember.setDepartLocationName(dto.getDepartLocationName());
            scheduleMember.setLongitude(dto.getLongitude());
            scheduleMember.setLatitude(dto.getLatitude());
        }
        else { // DB에 존재한다면
            CreateDepartLocationDto dto = CreateDepartLocationDto.entityToDto(metro.get());

            scheduleMember.setDepartLocationName(dto.getDepartLocationName());
            scheduleMember.setLongitude(dto.getLongitude());
            scheduleMember.setLatitude(dto.getLatitude());
        }

        //TODO : 출발장소들을 이용하여 중간장소 계산
        List<ScheduleMember> scheduleLocations = scheduleMemberQueryRepository.findByScheduleId(scheduleId);

        Double middleLatitude = 0.0;
        Double middleLongitude = 0.0;
        int cnt = 0;

        for (ScheduleMember sc : scheduleLocations) {
            if (sc.getLatitude() != null) {
                cnt++;
                middleLatitude += sc.getLatitude();        // 중간 경도 계산
                middleLongitude += sc.getLongitude();      // 중간 위도 계산
            }
        }

        if (middleLatitude != 0.0) {
            middleLatitude = middleLatitude / cnt;
            middleLongitude = middleLongitude / cnt;

            // 3개의 지하철역 정보를 가져옴
            List<JsonNode> subwayStation = findNearestStations(middleLatitude, middleLongitude);

            log.info("subwayStation size = {}", subwayStation.size());

            for (JsonNode subwayStationJson : subwayStation) {
                SubwayStationDto subwayStationDto = SubwayStationDto.toDto(subwayStationJson, schedule.get());
                Location location = SubwayStationDto.fromDto(subwayStationDto);
                Location location1 = locationCommandRepository.save(location);

                log.info("location = {}", location);
                log.info("location1 = {}", location1);

                Optional<Metro> metro1 = metroQueryRepository.findByName(location1.getName());
                List<Line> line = lineQueryRepository.findByMetroName(metro1.get().getName());

                for (Line l : line) {
                    DepartLocationMetroTransferDto dto = DepartLocationMetroTransferDto.toDto(location1, l);
                    MetroTransfer metroTransfer = DepartLocationMetroTransferDto.fromDto(dto);
                    metroTransferCommandRepository.save(metroTransfer);
                }
            }
        }


        log.info("middleLatitude = {}", middleLatitude);
        log.info("middleLongitude = {}", middleLongitude);

        em.flush();  // DB 반영
        em.clear();  // 영속성 컨텍스트 초기화

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


    // 카카오 api 활용하여 중간장소 역 3개 추출
    public List<JsonNode> findNearestStations(double latitude, double longitude)
        throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl("https://dapi.kakao.com/v2/local/search/category.json")
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
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.getBody());

        JsonNode  documents = json.get("documents");
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
        ResponseEntity<ZoomMeetingDto> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, ZoomMeetingDto.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
            throw new RuntimeException("Zoom API를 통해 회의를 생성하는데 실패했습니다. 응답: " + responseEntity.getBody());
        }

        String meetingLink = responseEntity.getBody().getJoinUrl();

        schedule.setMeetingPlatform(MeetingPlatform.ZOOM);
        schedule.setPlatformUrl(meetingLink);
        scheduleCommandRepository.save(schedule);

        CreateOnlineMeetingRoomDto dto = CreateOnlineMeetingRoomDto.toDto(meetingLink);
        return CreateOnlineMeetingRoomDto.fromDto(dto);
    }

    @Transactional
    public void WriteSuggestedLocation(Schedule schedule, WriteSuggestedLocationRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Principal user = (Principal) auth.getPrincipal();
        Member member = memberRepository.findById(user.getUsername()).orElseThrow();
        Optional<Metro> metro = metroQueryRepository.findByName(request.getLocationName());
//        ScheduleMember scheduleMember = scheduleMemberQueryRepository.findById(scheduleId).get();

        // DB에 존재하지 않는다면
        if (metro.isEmpty()) {
            WriteSuggestedLocationDto dto = WriteSuggestedLocationDto.requestToDto(request, schedule, member);

            Location location = WriteSuggestedLocationDto.fromDto(dto);
            locationCommandRepository.save(location);
        }
        else { // DB에 존재한다면
            Location location = WriteSuggestedLocationDto.metroToEntity(metro.get(), schedule, member);
            locationCommandRepository.save(location);
        }

    }
}
