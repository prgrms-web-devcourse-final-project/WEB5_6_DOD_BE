package com.grepp.spring.app.model.schedule.entity;

import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.schedule.code.MeetingPlatform;
import com.grepp.spring.app.model.schedule.code.ScheduleStatus;
import com.grepp.spring.app.model.schedule.dto.ModifyScheduleDto;
import com.grepp.spring.infra.entity.BaseEntity;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "Schedules")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Schedule extends BaseEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private LocalDateTime startTime;

    @Column()
    private LocalDateTime endTime;

    @Column()
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @Column
    private String location;

    @Column
    private String scheduleName;

    @Column(columnDefinition = "text")
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private MeetingPlatform meetingPlatform;

    @Column(columnDefinition = "text")
    private String platformUrl;

    @Column
    private String specificLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    // 일정이 삭제되면 ScheduleMember 도 같이 삭제
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleMember> scheduleMembers  = new ArrayList<>();

    // 일정이 삭제되면 일정에 포함된 장소도 삭제
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> locations  = new ArrayList<>();

    // 일정이 삭제되면 일정에 포함된 workspace 도 같이 삭제
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Workspace> workspaces = new ArrayList<>();

    // 일정이 삭제되면 일정에 포함된 환승정보 도 같이 삭제
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetroTransfer> metroTransfers = new ArrayList<>();

    // 일정이 삭제되면 일정에 포함된 투표정보 도 같이 삭제
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();
}
