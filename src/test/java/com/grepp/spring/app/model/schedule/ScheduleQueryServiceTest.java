package com.grepp.spring.app.model.schedule;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.grepp.spring.app.controller.api.auth.Provider;
import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.member.code.Role;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.ScheduleRole;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.code.WorkspaceType;
import com.grepp.spring.app.model.schedule.entity.Location;
import com.grepp.spring.app.model.schedule.entity.MetroTransfer;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.app.model.schedule.entity.Vote;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import com.grepp.spring.app.model.schedule.repository.LocationQueryRepository;
import com.grepp.spring.app.model.schedule.repository.MetroTransferQueryRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberQueryRepository;
import com.grepp.spring.app.model.schedule.repository.VoteQueryRepository;
import com.grepp.spring.app.model.schedule.repository.WorkspaceQueryRepository;
import com.grepp.spring.app.model.schedule.service.ScheduleQueryService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleQueryServiceTest {

    // 테스트 대상 클래스
    @InjectMocks
    private ScheduleQueryService scheduleQueryService;

    @Mock
    private ScheduleMemberQueryRepository scheduleMemberQueryRepository;

    @Mock
    private WorkspaceQueryRepository workspaceQueryRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private VoteQueryRepository voteQueryRepository;

    @Mock
    private LocationQueryRepository locationQueryRepository;

    @Mock
    private MetroTransferQueryRepository metroTransferQueryRepository;

    Member dummyMember1;
    Member dummyMember2;

    Event dummyEvent1;
    Group dummyGroup1;

    Schedule dummySchedule1;

    ScheduleMember dummyScheduleMember1;
    ScheduleMember dummyScheduleMember2;
    List<ScheduleMember> scheduleMembers;

    Workspace dummyWorkspace1;
    Workspace dummyWorkspace2;
    List<Workspace> workspaces;

    Location dummyLocation1;
    Location dummyLocation2;
    Location dummyLocation3;
    List<Location> locations;

    MetroTransfer dummyMetroTransfer1;
    MetroTransfer dummyMetroTransfer2;
    MetroTransfer dummyMetroTransfer3;
    List<MetroTransfer> metroTransfers;

    Vote dummyVote1;
    Vote dummyVote2;
    List<Vote> votes;

    @BeforeEach
    void setUp() {
        // 테스트용 더미 멤버 생성
        dummyMember1 = new Member();
        dummyMember1.setId("GOOGLE_1234");
        dummyMember1.setPassword("{noop}123qwe!@#");
        dummyMember1.setProvider(Provider.GOOGLE);
        dummyMember1.setRole(Role.ROLE_USER);
        dummyMember1.setEmail("test@gmail.com");
        dummyMember1.setName("하명도");
        dummyMember1.setProfileImageNumber(5);
        dummyMember1.setTel("010-1234-5678");

        dummyMember2 = new Member();
        dummyMember2.setId("KAKAO_1234");
        dummyMember2.setPassword("{noop}123qwe!@#");
        dummyMember2.setProvider(Provider.KAKAO);
        dummyMember2.setRole(Role.ROLE_USER);
        dummyMember2.setEmail("test@kakao.com");
        dummyMember2.setName("안준희");
        dummyMember2.setProfileImageNumber(6);
        dummyMember2.setTel("010-1111-2222");

        // 테스트용 더미 그룹 생성
        dummyGroup1 = new Group();
        dummyGroup1.setId(10000L);
        dummyGroup1.setIsGrouped(true);
        dummyGroup1.setDescription("테스트그룹임다.");
        dummyGroup1.setName("그루비룸");

        // 테스트용 더미 이벤트 생성
        dummyEvent1 = Event.createEvent(dummyGroup1, "스케줄입니다~", "스케줄 설명입니다!!", MeetingType.OFFLINE, 10);


        // 테스트용 더미 스케줄 생성
        dummySchedule1 = new Schedule();
        dummySchedule1.setId(10000L);
        dummySchedule1.setEvent(dummyEvent1);
        dummySchedule1.setStartTime(LocalDateTime.now());
        dummySchedule1.setEndTime(LocalDateTime.now().plusDays(1));
        dummySchedule1.setScheduleName("스케줄입니다~");
        dummySchedule1.setDescription("스케줄 설명입니다!!");
        dummySchedule1.setLocation("강남역");
        dummySchedule1.setSpecificLocation("강남역 스타벅스");
        dummySchedule1.setMeetingPlatform(MeetingPlatform.NONE);
        dummySchedule1.setPlatformUrl(null);
        dummySchedule1.setStatus(ScheduleStatus.FIXED);

        // 테스트용 더미 스케줄멤버 생성
        dummyScheduleMember1 = new ScheduleMember();
        dummyScheduleMember1.setId(10000L);
        dummyScheduleMember1.setMember(dummyMember1);
        dummyScheduleMember1.setSchedule(dummySchedule1);
        dummyScheduleMember1.setRole(ScheduleRole.ROLE_MASTER);
        dummyScheduleMember1.setDepartLocationName("강남역");
        dummyScheduleMember1.setLatitude(37.497958);
        dummyScheduleMember1.setLongitude(127.027539);
        dummyScheduleMember1.setName(dummyMember1.getName());

        dummyScheduleMember2 = new ScheduleMember();
        dummyScheduleMember2.setId(10001L);
        dummyScheduleMember2.setMember(dummyMember2);
        dummyScheduleMember2.setSchedule(dummySchedule1);
        dummyScheduleMember2.setRole(ScheduleRole.ROLE_MEMBER);
        dummyScheduleMember2.setDepartLocationName("건대입구역");
        dummyScheduleMember2.setLatitude(37.540882);
        dummyScheduleMember2.setLongitude(127.071103);
        dummyScheduleMember2.setName(dummyMember2.getName());

        scheduleMembers = List.of(dummyScheduleMember1, dummyScheduleMember2);

        // 테스트용 더미 워크스페이스 생성
        dummyWorkspace1 = new Workspace();
        dummyWorkspace1.setId(10000L);
        dummyWorkspace1.setType(WorkspaceType.GITHUB);
        dummyWorkspace1.setName("DOD github");
        dummyWorkspace1.setUrl("www.github.com");

        dummyWorkspace2 = new Workspace();
        dummyWorkspace2.setId(10001L);
        dummyWorkspace2.setType(WorkspaceType.FIGMA);
        dummyWorkspace2.setName("DOD figma");
        dummyWorkspace2.setUrl("www.figma.com");

        workspaces = List.of(dummyWorkspace1, dummyWorkspace2);


        // 테스트용 더미 장소 생성
        dummyLocation1 = new Location();
        dummyLocation1.setId(10000L);
        dummyLocation1.setSchedule(dummySchedule1);
        dummyLocation1.setLatitude(37.497958);
        dummyLocation1.setLongitude(127.027539);
        dummyLocation1.setName("역삼역");

        dummyLocation2 = new Location();
        dummyLocation2.setId(10001L);
        dummyLocation2.setSchedule(dummySchedule1);
        dummyLocation2.setLatitude(38.497958);
        dummyLocation2.setLongitude(127.027539);
        dummyLocation2.setName("강남역");

        dummyLocation3 = new Location();
        dummyLocation3.setId(10002L);
        dummyLocation3.setSchedule(dummySchedule1);
        dummyLocation3.setLatitude(38.478958);
        dummyLocation3.setLongitude(127.027839);
        dummyLocation3.setName("선릉역");

        locations = List.of(dummyLocation1, dummyLocation2, dummyLocation3);

        // 테스트용 더미 환승정보 생성
        dummyMetroTransfer1 = new MetroTransfer();
        dummyMetroTransfer1.setId(10000L);
        dummyMetroTransfer1.setLocation(dummyLocation1);
        dummyMetroTransfer1.setSchedule(dummySchedule1);
        dummyMetroTransfer1.setLineName("2");
        dummyMetroTransfer1.setColor("#00ASD");

        dummyMetroTransfer2 = new MetroTransfer();
        dummyMetroTransfer2.setId(10001L);
        dummyMetroTransfer2.setLocation(dummyLocation2);
        dummyMetroTransfer2.setSchedule(dummySchedule1);
        dummyMetroTransfer2.setLineName("5");
        dummyMetroTransfer2.setColor("#00B64D");

        dummyMetroTransfer3 = new MetroTransfer();
        dummyMetroTransfer3.setId(10002L);
        dummyMetroTransfer3.setLocation(dummyLocation3);
        dummyMetroTransfer3.setSchedule(dummySchedule1);
        dummyMetroTransfer3.setLineName("7");
        dummyMetroTransfer3.setColor("#00C84D");

        metroTransfers = List.of(dummyMetroTransfer1, dummyMetroTransfer2, dummyMetroTransfer3);

        // 테스트용 더미 투표 생성
        dummyVote1 = new Vote();
        dummyVote1.setId(10000L);
        dummyVote1.setLocation(dummyLocation1);
        dummyVote1.setScheduleMember(dummyScheduleMember1);
        dummyVote1.setSchedule(dummySchedule1);

        dummyVote2 = new Vote();
        dummyVote2.setId(10001L);
        dummyVote2.setLocation(dummyLocation2);
        dummyVote2.setScheduleMember(dummyScheduleMember2);
        dummyVote2.setSchedule(dummySchedule1);

        votes = List.of(dummyVote1, dummyVote2);

    }


    @DisplayName("일정 조회 ")
    @Test
    void showScheduleTest() {

        // Given
        when(scheduleMemberQueryRepository.findByScheduleId(10000L)).thenReturn(scheduleMembers);
        when(workspaceQueryRepository.findAllByScheduleId(10000L)).thenReturn(workspaces);
        when(eventRepository.findById(dummyEvent1.getId())).thenReturn(Optional.of(dummyEvent1));


        // When
        var response = scheduleQueryService.showSchedule(dummySchedule1,"GOOGLE_1234");

        // Then
        assertNotNull(response);
        Assertions.assertThat(response.getScheduleName()).isEqualTo("스케줄입니다~");
    }

    @DisplayName("중간장소 후보 조회")
    @Test
    void showSuggestedLocationsTest() {

        // Given
        when(scheduleMemberQueryRepository.findByScheduleId(10000L)).thenReturn(scheduleMembers);
        when(voteQueryRepository.findByScheduleId(10000L)).thenReturn(votes);

        when(locationQueryRepository.findByScheduleId(10000L)).thenReturn(locations);
        when(metroTransferQueryRepository.findByLocationId(10000L)).thenReturn(List.of(dummyMetroTransfer1));
        when(metroTransferQueryRepository.findByLocationId(10001L)).thenReturn(List.of(dummyMetroTransfer2));
        when(metroTransferQueryRepository.findByLocationId(10002L)).thenReturn(List.of(dummyMetroTransfer3));


        // When
        var response = scheduleQueryService.showSuggestedLocation(10000L);

        // Then
        assertNotNull(response);
        Assertions.assertThat(response.getNoVoteCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("투표한 인원 조회")
    void showScheduleMemberTest() {

        // Given
        when(scheduleMemberQueryRepository.findByScheduleId(10000L)).thenReturn(scheduleMembers);
        when(voteQueryRepository.findByScheduleMemberId(10000L)).thenReturn(dummyVote1);
        when(voteQueryRepository.findByScheduleMemberId(10001L)).thenReturn(dummyVote2);

        // When
        var response = scheduleQueryService.findVoteMembers(10000L);

        // Then
        assertNotNull(response);
        Assertions.assertThat(response.getVoteMembersList()).size().isEqualTo(2);
    }
}

