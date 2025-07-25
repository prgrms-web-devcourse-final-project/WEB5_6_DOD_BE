package com.grepp.spring.app.model.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.grepp.spring.app.controller.api.auth.Provider;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.group.repository.GroupCommandRepository;
import com.grepp.spring.app.model.group.repository.GroupMemberRepository;
import com.grepp.spring.app.model.group.service.GroupCommandService;
import com.grepp.spring.app.model.member.code.Role;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.infra.error.exceptions.member.WithdrawNotAllowedException;
import com.grepp.spring.infra.utils.RandomPicker;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    @Spy
    private GroupCommandService groupCommandService;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private GroupCommandRepository groupCommandRepository;

    // 테스트용 객체
    private Member testMember1;
    private Member testMember2;
    private Member testMember3;
    private Member testMember4;
    private Member testMember5;
    private Group testGroup;
    private GroupMember testGroupAdmin;
    private GroupMember testGroupOtherLeader1;
    private GroupMember testGroupOtherLeader2;
    private GroupMember testGroupMember1;
    private GroupMember testGroupMember2;

    @BeforeEach
    void setUp() {
        // 테스트용 더미 멤버 - 이하 5개
        testMember1 = new Member();
        testMember1.setId("GOOGLE_09172213");
        testMember1.setName("이서준");
        testMember1.setProvider(Provider.GOOGLE);
        testMember1.setEmail("seojun@gmail.com");
        testMember1.setRole(Role.ROLE_USER);
        testMember1.setProfileImageNumber(1);
        testMember1.setPassword("{noop}123qwe!@#");

        testMember2 = new Member();
        testMember2.setId("KAKAO_11968923");
        testMember2.setName("이강현");
        testMember2.setProvider(Provider.KAKAO);
        testMember2.setEmail("kanghyeon@kakao.com");
        testMember2.setRole(Role.ROLE_USER);
        testMember2.setProfileImageNumber(2);
        testMember2.setPassword("{noop}123qwe!@#");

        testMember3 = new Member();
        testMember3.setId("GOOGLE_31642293");
        testMember3.setName("안준희");
        testMember3.setProvider(Provider.GOOGLE);
        testMember3.setEmail("junhui@gmail.com");
        testMember3.setRole(Role.ROLE_USER);
        testMember3.setProfileImageNumber(0);
        testMember3.setPassword("{noop}123qwe!@#");

        testMember4 = new Member();
        testMember4.setId("KAKAO_72895917");
        testMember4.setName("정서윤");
        testMember4.setProvider(Provider.KAKAO);
        testMember4.setEmail("seoyoon@kakao.com");
        testMember4.setRole(Role.ROLE_USER);
        testMember4.setProfileImageNumber(5);
        testMember4.setPassword("{noop}123qwe!@#");

        testMember5 = new Member();
        testMember5.setId("GOOGLE_76025618");
        testMember5.setName("최동준");
        testMember5.setProvider(Provider.GOOGLE);
        testMember5.setEmail("dongjun@gmail.com");
        testMember5.setRole(Role.ROLE_USER);
        testMember5.setProfileImageNumber(7);
        testMember5.setPassword("{noop}123qwe!@#");

        // 테스트용 더미 그룹
        testGroup = new Group();
        testGroup.setId(10077L);
        testGroup.setName("그래도 해야지 어떡해");
        testGroup.setDescription("프로그래머스 데브코스 최종 프로젝트 7팀입니다.");
        testGroup.setIsGrouped(true);

        // 테스트용 더미 그룹 멤버 - 그룹 관리자
        testGroupAdmin = new GroupMember();
        testGroupAdmin.setId(10070L);
        testGroupAdmin.setRole(GroupRole.GROUP_LEADER);
        testGroupAdmin.setMember(testMember1);
        testGroupAdmin.setGroup(testGroup);
        testGroupAdmin.setGroupAdmin(true);
        // 테스트용 더미 그룹 멤버 - 그룹 리더
        testGroupOtherLeader1 = new GroupMember();
        testGroupOtherLeader1.setId(10071L);
        testGroupOtherLeader1.setRole(GroupRole.GROUP_LEADER);
        testGroupOtherLeader1.setMember(testMember2);
        testGroupOtherLeader1.setGroup(testGroup);
        testGroupOtherLeader1.setGroupAdmin(false);

        testGroupOtherLeader2 = new GroupMember();
        testGroupOtherLeader2.setId(10072L);
        testGroupOtherLeader2.setRole(GroupRole.GROUP_LEADER);
        testGroupOtherLeader2.setMember(testMember3);
        testGroupOtherLeader2.setGroup(testGroup);
        testGroupOtherLeader2.setGroupAdmin(false);

        // 테스트용 더미 그룹 멤버
        testGroupMember1 = new GroupMember();
        testGroupMember1.setId(10073L);
        testGroupMember1.setRole(GroupRole.GROUP_MEMBER);
        testGroupMember1.setMember(testMember4);
        testGroupMember1.setGroup(testGroup);
        testGroupMember1.setGroupAdmin(false);

        testGroupMember2 = new GroupMember();
        testGroupMember2.setId(10074L);
        testGroupMember2.setRole(GroupRole.GROUP_MEMBER);
        testGroupMember2.setMember(testMember5);
        testGroupMember2.setGroup(testGroup);
        testGroupMember2.setGroupAdmin(false);

    }

    @Test
    @DisplayName("그룹 관리자가 탈퇴 시 다른 리더에게 관리자 권한 위임")
    void handleGroupWithdrawalDelegate() {
        // 특정 멤버가 관리자인 그룹 조회
        when(groupMemberRepository.findGroupsByMemberAndAdmin(testMember1))
            .thenReturn(Arrays.asList(testGroup));
        // 그 그룹의 모든 멤버 조회
        List<GroupMember> allGroupMembers = Arrays.asList(
            testGroupAdmin, testGroupOtherLeader1, testGroupOtherLeader2, testGroupMember1,
            testGroupMember2
        );
        when(groupMemberRepository.findByGroup(testGroup))
            .thenReturn(allGroupMembers);
        // 그룹에서 관리자가 아닌 다른 리더를 조회
        List<GroupMember> otherLeaders = Arrays.asList(testGroupOtherLeader1, testGroupOtherLeader2);
        when(groupMemberRepository.findByGroupAndLeaderAndMemberNot(testGroup, testMember1))
            .thenReturn(otherLeaders);

        // 위임 받을 리더가 임의로 선택됨.
        try (MockedStatic<RandomPicker> mockedStatic = mockStatic(RandomPicker.class)) {
            mockedStatic.when(() -> RandomPicker.pickRandom(anyList()))
                .thenReturn(testGroupOtherLeader1);
            // 실제 테스트 대상 메서드 호출. 정상적으로 응답이 나오는가?
            groupCommandService.handleGroupWithdrawal(testMember1);
            // 결과 검증
            // 그룹이 삭제되지 않았음을 검증
            verify(groupCommandRepository, never()).delete(any(Group.class));
            // grantAdminRole 메서드가 호출되었는지 (권한 위임이 발생했는지) 검증
            assertTrue(testGroupOtherLeader1.getGroupAdmin(), "testGroupOtherLeader1의 GroupAdmin이 true로 나와야 한다.");
            // 수정된 권한 정보가 저장되었는지 검증
            verify(groupMemberRepository, times(1)).save(testGroupOtherLeader1);
        }
    }

    @Test
    @DisplayName("그룹 관리자가 탈퇴 시 권한을 위임할 다른 리더가 없는 경우")
    void handleGroupWithdrawalNotExistOtherLeaders() {
        // testMember1이 관리자인 그룹 조회 시 testGroup Return
        when(groupMemberRepository.findGroupsByMemberAndAdmin(testMember1))
            .thenReturn(Arrays.asList(testGroup)); // 추후 그룹 추가 예정
        // testGroup 의 모든 멤버 리스트 Return
        List<GroupMember> allGroupMembers = Arrays.asList(
            testGroupAdmin, testGroupOtherLeader1, testGroupOtherLeader2, testGroupMember1,
            testGroupMember2
        );
        when(groupMemberRepository.findByGroup(testGroup))
            .thenReturn(allGroupMembers);

        // testGroup 에서 testMember1을 제외한 다른 리더를 조회하면 빈 리스트 Return
        when(groupMemberRepository.findByGroupAndLeaderAndMemberNot(testGroup, testMember1))
            .thenReturn(Collections.emptyList());
        // 실제 메서드를 호출해서 예외가 발생하는지 확인
        WithdrawNotAllowedException thrownException = assertThrows(WithdrawNotAllowedException.class, () ->
            groupCommandService.handleGroupWithdrawal(testMember1)
        );
        // 결과 검증
        // 예외 메시지가 정확한가?
        assertEquals("이 그룹에 관리자 권한을 위임할 수 있는 리더를 추가해주세요.", thrownException.getMessage());
        // 예외 객체에 위임이 불가능한 그룹이 포함되어 있는지
        assertNotNull(thrownException.getLeaderGroups());
        assertEquals(1, thrownException.getLeaderGroups().size());
        assertTrue(thrownException.getLeaderGroups().contains(testGroup), "예외 그룹에 testGroup 이 포함되어야 함.");
        // 그럼 그룹은 삭제되면 안됨! 그룹 삭제 메서드가 호출되지 않았는지 검증
        verify(groupCommandRepository, never()).delete(any(Group.class));
        // 누군가에게 관리자 권한을 주고, 그 정보를 저장하는 메서드도 호출되지 않았는지 검증
        verify(groupMemberRepository, never()).save(any(GroupMember.class));
        // 리더 중 랜덤으로 한 명을 뽑는 RandomPicker 도 호출되면 안됨. 근데 굳이 검증 안해도 될 것 같기도?
        verifyNoInteractions(mock(RandomPicker.class));
    }

    @Test
    @DisplayName("그룹 관리자가 그룹의 유일한 멤버인 경우")
    void handleGroupWithdrawalUniqueMember() {
        // testMember1이 관리자인 그룹 조회 시 testGroup Return
        when(groupMemberRepository.findGroupsByMemberAndAdmin(testMember1))
            .thenReturn(Arrays.asList(testGroup));
        // testGroup 의 모든 멤버 조회 시 리스트에 본인만 있음.
        List<GroupMember> allGroupMembers = Collections.singletonList(testGroupAdmin);
        when(groupMemberRepository.findByGroup(testGroup))
            .thenReturn(allGroupMembers);
        // 그룹의 멤버에 본인이 포함되어 있는지 검증
        assertTrue(allGroupMembers.contains(testGroupAdmin));
//        // 그룹의 멤버가 본인 한명인지 검증
//        assertEquals(1, allGroupMembers.size()); // singletonList 이기 때문에 필요없음.
        // 실제 메서드 호출
        groupCommandService.handleGroupWithdrawal(testMember1);
        // 결과 검증
        // groupCommandRepository.delete(testGroup)이 호출되었는지 검증
        verify(groupCommandRepository, times(1)).delete(testGroup);
        // 누군가에게 관리자 권한을 주고, 그 정보를 저장하는 메서드도 호출되지 않았는지 검증
        verify(groupMemberRepository, never()).save(any(GroupMember.class));
    }
}