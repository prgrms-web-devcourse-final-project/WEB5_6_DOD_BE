package com.grepp.spring.app.model.group.dto;

import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.group.entity.GroupMember;
import com.grepp.spring.app.model.member.entity.Member;

public class GroupMemberCreateDto {

    public static GroupMember toEntity(Group group, Member member){
        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setMember(member);
        groupMember.setGroupAdmin(true);
        groupMember.setRole(GroupRole.GROUP_LEADER);

        return groupMember;
    }

}
