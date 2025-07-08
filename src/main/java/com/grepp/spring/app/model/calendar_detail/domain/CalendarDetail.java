package com.grepp.spring.app.model.calendar_detail.domain;

import com.grepp.spring.app.model.calendar.domain.Calendar;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "CalendarDetails")
@Getter
@Setter
public class CalendarDetail {

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
    private String title;

    @Column(nullable = false)
    private String startDatetime;

    @Column(nullable = false)
    private String endDatetime;

    @Column(nullable = false)
    private LocalDateTime syncedAt;

    @Column(nullable = false)
    private Boolean isAllDay;

    @Column(nullable = false, columnDefinition = "text")
    private String externalEtag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

}
