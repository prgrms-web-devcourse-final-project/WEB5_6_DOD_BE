package com.grepp.spring.app.model.group.code;

public enum GroupRole{
    GROUP_LEADER,
    GROUP_MEMBER;

    public boolean isGroupLeader(){
        return this==GROUP_LEADER;
    }
}
