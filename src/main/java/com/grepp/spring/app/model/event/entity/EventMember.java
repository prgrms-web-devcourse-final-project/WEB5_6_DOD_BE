package com.grepp.spring.app.model.event.entity;

import com.grepp.spring.app.model.event.code.Role;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.infra.entity.BaseEntity;
import com.grepp.spring.infra.error.exceptions.event.AlreadyCompletedScheduleException;
import com.grepp.spring.infra.response.EventErrorCode;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "EventMembers")
@Getter
@Setter
public class EventMember extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Boolean confirmed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    // 이벤트멤버가 삭제되면 tempSchedule 도 삭제
    @OneToMany(mappedBy = "eventMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TempSchedule> tempSchedules = new ArrayList<>();

    public void confirm() {
        if (this.confirmed) {
            throw new AlreadyCompletedScheduleException(EventErrorCode.ALREADY_COMPLETED_SCHEDULE);
        }
        this.confirmed = true;
    }

}
