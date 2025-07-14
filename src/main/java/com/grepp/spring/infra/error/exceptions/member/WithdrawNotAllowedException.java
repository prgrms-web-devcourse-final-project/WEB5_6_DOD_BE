package com.grepp.spring.infra.error.exceptions.member;

import com.grepp.spring.app.model.group.entity.Group;
import java.util.List;

public class WithdrawNotAllowedException extends RuntimeException {

    private final List<Group> leaderGroups;

    public WithdrawNotAllowedException(String message, List<Group> leaderGroups) {
        super(message);
        this.leaderGroups = leaderGroups;
    }

    public List<Group> getLeaderGroups() {
        return leaderGroups;
    }
}
