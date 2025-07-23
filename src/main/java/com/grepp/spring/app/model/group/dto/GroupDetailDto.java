package com.grepp.spring.app.model.group.dto;

import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GroupDetailDto {
    private Long groupId;
    private String groupName;
    private String description;
    private Integer groupMemberNum;
    private Integer leaderProfileImage;

    public static GroupDetailDto from(Group group, List<GroupMember> allGroupMembers) {

        // 이 그룹에 속한 멤버 수 카운트
        int memberCount = (int) allGroupMembers.stream()
            .filter(gm -> gm.getGroup().getId().equals(group.getId()))
            .count();

        // 그룹장 이미지 찾기
        Integer leaderProfileImage = allGroupMembers.stream()
            .filter(gm -> gm.getGroup().getId().equals(group.getId()))
            .filter(gm -> gm.getRole() == GroupRole.GROUP_LEADER)
            .findFirst()
            .map(gm -> gm.getMember().getProfileImageNumber())
            .orElse(null);

        return GroupDetailDto.builder()
            .groupId(group.getId())
            .groupName(group.getName())
            .description(group.getDescription())
            .groupMemberNum(memberCount)      // 멤버 수 세팅
            .leaderProfileImage(leaderProfileImage) // 그룹장 이미지만 추가
            .build();
    }

}
