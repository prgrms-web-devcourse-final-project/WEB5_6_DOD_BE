package com.grepp.spring.app.model.event.entity;

import com.grepp.spring.infra.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Table(name = "TempSchedules")
@Getter
@Setter
public class TempSchedule extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Long timeBit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_member_id")
    private EventMember eventMember;

}
