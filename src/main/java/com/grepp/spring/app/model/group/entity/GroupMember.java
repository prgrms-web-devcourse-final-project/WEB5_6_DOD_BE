package com.grepp.spring.app.model.group.entity;

import com.grepp.spring.app.controller.api.group.payload.request.ControlGroupRoleRequest;
import com.grepp.spring.app.model.group.code.GroupRole;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.infra.entity.BaseEntity;
import com.grepp.spring.infra.error.exceptions.group.NotGroupLeaderException;
import com.grepp.spring.infra.error.exceptions.group.UserGroupLeaderException;
import com.grepp.spring.infra.response.GroupErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "GroupMembers")
@Getter
@Setter
@NoArgsConstructor
public class GroupMember extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    private Boolean groupAdmin;


    public GroupMember(Member member, Group group, GroupRole groupRole, boolean isAdmin) {
        this.role =groupRole;
        this.member = member;
        this.group = group;
        this.groupAdmin = isAdmin;
    }

    public static GroupMember createGroupMemberLeader(Group group, Member member) {
        return new GroupMember(member,group, GroupRole.GROUP_LEADER, true);
    }

    public static GroupMember createGroupMemberMember(Group group, Member member) {
        return new GroupMember(member,group, GroupRole.GROUP_MEMBER, false);
    }


    public boolean isGroupLeader() {
        return role.isGroupLeader();
    }

    public void isGroupLeaderOrThrow() {
        if(!isGroupLeader()){
            throw new NotGroupLeaderException(GroupErrorCode.NOT_GROUP_LEADER);
        }
    }

    public void isNotGroupLeaderOrThrow(){
        if(isGroupLeader()){
            throw new UserGroupLeaderException(GroupErrorCode.USER_GROUP_LEADER);
        }
    }

    public void updateGroupRole(ControlGroupRoleRequest request) {
        this.role = request.getGroupRole();
    }


    public void delegateAdmin(GroupMember groupMember) {
        if(this.groupAdmin){
            this.groupAdmin = false;
            groupMember.groupAdmin = true;
        }
    }
}
