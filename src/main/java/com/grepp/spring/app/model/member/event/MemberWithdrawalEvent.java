package com.grepp.spring.app.model.member.event;

import com.grepp.spring.app.model.member.entity.Member;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MemberWithdrawalEvent extends ApplicationEvent {

    private final Member member;

    public MemberWithdrawalEvent(Object source, Member member) {
        super(source);
        this.member = member;
    }
}