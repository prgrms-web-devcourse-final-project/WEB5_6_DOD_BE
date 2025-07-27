package com.grepp.spring.app.model.group.service;

import com.grepp.spring.app.controller.api.auth.Provider;
import com.grepp.spring.app.controller.api.group.payload.request.ControlGroupRoleRequest;
import com.grepp.spring.app.controller.api.group.payload.request.CreateGroupRequest;
import com.grepp.spring.app.controller.api.group.payload.request.ModifyGroupInfoRequest;
import com.grepp.spring.app.controller.api.group.payload.response.CreateGroupResponse;
import com.grepp.spring.app.controller.api.group.payload.response.InviteGroupMemberResponse;
import com.grepp.spring.app.controller.api.group.payload.response.ModifyGroupInfoResponse;
import com.grepp.spring.app.model.event.repository.EventMemberRepository;
import com.grepp.spring.app.model.event.repository.EventRepository;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberCommandRepository;
import com.grepp.spring.app.model.member.code.Role;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.repository.MemberRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleCommandRepository;
import com.grepp.spring.app.model.schedule.repository.ScheduleMemberCommandRepository;
import com.grepp.spring.infra.error.exceptions.group.NotGroupLeaderException;
import com.grepp.spring.infra.error.exceptions.group.UserAlreadyInGroupException;
import com.grepp.spring.infra.error.exceptions.group.UserGroupLeaderException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupCommandServiceTest {

    @InjectMocks
    @Spy
    GroupCommandService groupCommandService;

    @Mock
    private GroupCommandRepository groupCommandRepository;

    @Mock
    private GroupMemberCommandRepository groupMemberCommandRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMemberRepository eventMemberRepository;

    @Mock
    private ScheduleCommandRepository scheduleCommandRepository;

    @Mock
    private ScheduleMemberCommandRepository scheduleMemberCommandRepository;

    Member dummyMember1;
    Member dummyMember2;
    Group dummyGroup1;
    GroupMember dummyGroupMember1;
    GroupMember dummyGroupMember2;
    GroupMember dummyGroupMember3;
    GroupMember dummyGroupMember4;

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

        dummyGroup1 = new Group();
        dummyGroup1.setId(10000L);
        dummyGroup1.setIsGrouped(true);
        dummyGroup1.setDescription("테스트그룹임다.");
        dummyGroup1.setName("그루비룸");

        dummyGroupMember1 = new GroupMember();
        dummyGroupMember1.setId(20000L);
        dummyGroupMember1.setGroup(dummyGroup1);
        dummyGroupMember1.setGroupAdmin(true);
        dummyGroupMember1.setMember(dummyMember1);
        dummyGroupMember1.setRole(GroupRole.GROUP_LEADER);

        dummyGroupMember2 = new GroupMember();
        dummyGroupMember2.setId(20000L);
        dummyGroupMember2.setGroup(dummyGroup1);
        dummyGroupMember2.setGroupAdmin(false);
        dummyGroupMember2.setMember(dummyMember1);
        dummyGroupMember2.setRole(GroupRole.GROUP_MEMBER);

        dummyGroupMember3 = new GroupMember();
        dummyGroupMember3.setId(20001L);
        dummyGroupMember3.setGroup(dummyGroup1);
        dummyGroupMember3.setGroupAdmin(false);
        dummyGroupMember3.setMember(dummyMember2);
        dummyGroupMember3.setRole(GroupRole.GROUP_LEADER);

        dummyGroupMember4 = new GroupMember();
        dummyGroupMember4.setId(20001L);
        dummyGroupMember4.setGroup(dummyGroup1);
        dummyGroupMember4.setGroupAdmin(false);
        dummyGroupMember4.setMember(dummyMember2);
        dummyGroupMember4.setRole(GroupRole.GROUP_MEMBER);

    }


    @Test
    void registGroupSuccess() {
        // given
        doReturn(dummyMember1)
            .when(groupCommandService).findHttpRequestMemberOrThrow();

        when(groupCommandRepository.save(any(Group.class)))
            .thenAnswer(invocation -> {
                Group g = invocation.getArgument(0);
                g.setId(42L);
                return g;
            });

        when(groupMemberCommandRepository.save(any(GroupMember.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        CreateGroupRequest request = new CreateGroupRequest("스터디", "자바 스터디 그룹");

        // when
        CreateGroupResponse response = groupCommandService.registGroup(request);

        // then
        assertEquals(42L, response.getGroupId());
        assertEquals("스터디", response.getGroupName());
        assertEquals("자바 스터디 그룹", response.getDescription());
    }


    @Test
    void addGroupMemberSuccess() {
        // given
        doReturn(dummyMember1)
            .when(groupCommandService).findHttpRequestMemberOrThrow();

        when(groupCommandRepository.findById(10000L))
            .thenReturn(Optional.of(dummyGroup1));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "GOOGLE_1234"))
            .thenReturn(Optional.empty());

        when(groupMemberCommandRepository.save(any(GroupMember.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        InviteGroupMemberResponse response = groupCommandService.addGroupMember(10000L);

        // then
        assertEquals(10000L, response.getGroupId());
        assertEquals("GOOGLE_1234", response.getMemberId());
        assertEquals("하명도", response.getMemberName());
        // ArgumentCaptor로 저장된 groupMember 검증
        ArgumentCaptor<GroupMember> captor = ArgumentCaptor.forClass(GroupMember.class);
        verify(groupMemberCommandRepository, times(1)).save(captor.capture());
        GroupMember savedGroupMember = captor.getValue();
        assertEquals(dummyGroup1, savedGroupMember.getGroup());
        assertEquals(dummyMember1, savedGroupMember.getMember());
        assertEquals(GroupRole.GROUP_MEMBER, savedGroupMember.getRole()); // 예: 기본 역할 검증
    }

    @Test
    void addGroupMemberFailAlreadyInGroup() {
        // given
        doReturn(dummyMember1)
            .when(groupCommandService).findHttpRequestMemberOrThrow();

        when(groupCommandRepository.findById(10000L))
            .thenReturn(Optional.of(dummyGroup1));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "GOOGLE_1234"))
            .thenReturn(Optional.of(dummyGroupMember1));
        // when & then
        assertThrows(UserAlreadyInGroupException.class, () -> {
            groupCommandService.addGroupMember(10000L);
        });
        verify(groupMemberCommandRepository, never()).save(any());
    }


    @Test
    void deleteGroupSuccess() {
        // given
        doReturn(dummyMember1)
            .when(groupCommandService).findHttpRequestMemberOrThrow();

        when(groupCommandRepository.findById(10000L))
            .thenReturn(Optional.of(dummyGroup1));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "GOOGLE_1234"))
            .thenReturn(Optional.of(dummyGroupMember1));

        dummyGroupMember1.isGroupLeaderOrThrow();

        // when
        groupCommandService.deleteGroup(10000L);

        // then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(groupCommandRepository, times(1)).deleteById(captor.capture());
        assertEquals(10000L, captor.getValue());
    }

    @Test
    void deleteGroupFailNotGroupLeader() {
        // given
        doReturn(dummyMember1)
            .when(groupCommandService).findHttpRequestMemberOrThrow();

        when(groupCommandRepository.findById(10000L))
            .thenReturn(Optional.of(dummyGroup1));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "GOOGLE_1234"))
            .thenReturn(Optional.of(dummyGroupMember2));

        // when & then
        assertThrows(NotGroupLeaderException.class, () -> {
            groupCommandService.deleteGroup(10000L);
        });
        verify(groupCommandRepository, never()).deleteById(any());
    }


    @Test
    void modifyGroupSuccess() {
        // given
        doReturn(dummyMember1)
            .when(groupCommandService).findHttpRequestMemberOrThrow();

        when(groupCommandRepository.findById(10000L))
            .thenReturn(Optional.of(dummyGroup1));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "GOOGLE_1234"))
            .thenReturn(Optional.of(dummyGroupMember1));

        dummyGroupMember1.isGroupLeaderOrThrow();

        when(groupCommandRepository.save(any(Group.class)))
            .thenAnswer(invocation -> {
                return invocation.<Group>getArgument(0);
            });

        ModifyGroupInfoRequest request = new ModifyGroupInfoRequest("스터디", "자바 스터디 그룹");

        // when
        ModifyGroupInfoResponse response = groupCommandService.modifyGroup(dummyGroup1.getId(),
            request);

        // then
        assertEquals(dummyGroup1.getId(), response.getGroupId());
        assertEquals("스터디", response.getGroupName());
        assertEquals("자바 스터디 그룹", response.getDescription());
    }

    @Test
    void modifyGroupFailNotGroupLeader() {
        // given
        doReturn(dummyMember1)
            .when(groupCommandService).findHttpRequestMemberOrThrow();

        when(groupCommandRepository.findById(10000L))
            .thenReturn(Optional.of(dummyGroup1));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "GOOGLE_1234"))
            .thenReturn(Optional.of(dummyGroupMember2));

        // when & then
        assertThrows(NotGroupLeaderException.class, () -> {
            groupCommandService.modifyGroup(10000L, new ModifyGroupInfoRequest("스터디", ""));
        });
        verify(groupCommandRepository, never()).save(any());
    }


    @Test
    void deportMemberSuccess() {
        // given
        doReturn(dummyMember1)
            .when(groupCommandService).findHttpRequestMemberOrThrow();

        when(groupCommandRepository.findById(10000L))
            .thenReturn(Optional.of(dummyGroup1));

        when(memberRepository.findById("KAKAO_1234"))
            .thenReturn(Optional.of(dummyMember2));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "GOOGLE_1234"))
            .thenReturn(Optional.of(dummyGroupMember1));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "KAKAO_1234"))
            .thenReturn(Optional.of(dummyGroupMember4));

        // when
        groupCommandService.deportMember(10000L, "KAKAO_1234");

        // then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(groupMemberCommandRepository, times(1)).deleteByGroupAndMemberId(eq(dummyGroup1),
            captor.capture());
        assertEquals("KAKAO_1234", captor.getValue());
    }

    @Test
    void deportMemberFailNotGroupLeader() {
        // given
        doReturn(dummyMember1)
            .when(groupCommandService).findHttpRequestMemberOrThrow();

        when(groupCommandRepository.findById(10000L))
            .thenReturn(Optional.of(dummyGroup1));

        when(memberRepository.findById("KAKAO_1234"))
            .thenReturn(Optional.of(dummyMember2));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "GOOGLE_1234"))
            .thenReturn(Optional.of(dummyGroupMember2));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "KAKAO_1234"))
            .thenReturn(Optional.of(dummyGroupMember4));

        // when & then
        assertThrows(NotGroupLeaderException.class, () -> {
            groupCommandService.deportMember(10000L, "KAKAO_1234");
        });

        // then
        verify(groupMemberCommandRepository, never()).deleteByGroupAndMemberId(dummyGroup1,
            "KAKAO_1234");
    }

    @Test
    void deportMemberFailTargetIsGroupLeader() {
        // given
        doReturn(dummyMember1)
            .when(groupCommandService).findHttpRequestMemberOrThrow();

        when(groupCommandRepository.findById(10000L))
            .thenReturn(Optional.of(dummyGroup1));

        when(memberRepository.findById("KAKAO_1234"))
            .thenReturn(Optional.of(dummyMember2));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "GOOGLE_1234"))
            .thenReturn(Optional.of(dummyGroupMember1));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "KAKAO_1234"))
            .thenReturn(Optional.of(dummyGroupMember3));

        // when & then
        assertThrows(UserGroupLeaderException.class, () -> {
            groupCommandService.deportMember(10000L, "KAKAO_1234");
        });

        // then
        verify(groupMemberCommandRepository, never()).deleteByGroupAndMemberId(dummyGroup1,
            "KAKAO_1234");
    }

    @Test
    void modifyGroupRoleSuccess() {
        // given
        doReturn(dummyMember1)
            .when(groupCommandService).findHttpRequestMemberOrThrow();

        when(groupCommandRepository.findById(10000L))
            .thenReturn(Optional.of(dummyGroup1));

        when(memberRepository.findById("KAKAO_1234"))
            .thenReturn(Optional.of(dummyMember2));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "GOOGLE_1234"))
            .thenReturn(Optional.of(dummyGroupMember1));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "KAKAO_1234"))
            .thenReturn(Optional.of(dummyGroupMember4));

        // when
        groupCommandService.modifyGroupRole(10000L,
            new ControlGroupRoleRequest("KAKAO_1234", GroupRole.GROUP_LEADER));

        // then
        verify(groupMemberCommandRepository, times(2)).save(any());
        assertEquals(GroupRole.GROUP_LEADER, dummyGroupMember4.getRole());
        assertEquals(false, dummyGroupMember4.getGroupAdmin());
        assertEquals(true, dummyGroupMember1.getGroupAdmin());
    }

    @Test
    void modifyGroupRoleSuccessDelegateSuperLeader() {
        // given
        doReturn(dummyMember2)
            .when(groupCommandService).findHttpRequestMemberOrThrow();

        when(groupCommandRepository.findById(10000L))
            .thenReturn(Optional.of(dummyGroup1));

        when(memberRepository.findById("GOOGLE_1234"))
            .thenReturn(Optional.of(dummyMember1));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "GOOGLE_1234"))
            .thenReturn(Optional.of(dummyGroupMember1));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "KAKAO_1234"))
            .thenReturn(Optional.of(dummyGroupMember3));

        // when
        groupCommandService.modifyGroupRole(10000L,
            new ControlGroupRoleRequest("GOOGLE_1234", GroupRole.GROUP_MEMBER));

        // then
        verify(groupMemberCommandRepository, times(2)).save(any());
        assertEquals(GroupRole.GROUP_MEMBER, dummyGroupMember1.getRole());
        assertEquals(false, dummyGroupMember1.getGroupAdmin());
        assertEquals(true, dummyGroupMember3.getGroupAdmin());
    }

    @Test
    void modifyGroupRoleFailNotGroupLeader() {
        // given
        doReturn(dummyMember1)
            .when(groupCommandService).findHttpRequestMemberOrThrow();

        when(groupCommandRepository.findById(10000L))
            .thenReturn(Optional.of(dummyGroup1));

        when(memberRepository.findById("KAKAO_1234"))
            .thenReturn(Optional.of(dummyMember2));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "GOOGLE_1234"))
            .thenReturn(Optional.of(dummyGroupMember2));

        when(groupMemberCommandRepository.findByGroupIdAndMemberId(10000L, "KAKAO_1234"))
            .thenReturn(Optional.of(dummyGroupMember4));

        // when & then
        assertThrows(NotGroupLeaderException.class, () -> {
            groupCommandService.modifyGroupRole(10000L,
                new ControlGroupRoleRequest("KAKAO_1234", GroupRole.GROUP_MEMBER));
        });

        // then
        verify(groupMemberCommandRepository, never()).save(any());
    }


    @Test
    void transferSchedule() {
    }


    @Test
    void withdrawGroup() {
    }
}