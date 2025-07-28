package com.grepp.spring.app.model.event.entity;

import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.group.entity.Group;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.infra.entity.BaseEntity;
import com.grepp.spring.infra.error.exceptions.event.InvalidEventDataException;
import com.grepp.spring.infra.response.EventErrorCode;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "Events")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    @Column(nullable = false)
    private Integer maxMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Group group;

    // 이벤트가 삭제되면 그 후보 시간대도 삭제
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateDate> candidateDates = new ArrayList<>();

    // 이벤트가 삭제되면 이벤트로부터 생성된 일정도 삭제
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    // 이벤트가 삭제되면 eventMember 도 삭제
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventMember> eventMembers = new ArrayList<>();

    private Event(Group group, String title, String description,
                  MeetingType meetingType, Integer maxMember) {
        validateForCreation(title, meetingType, maxMember);

        this.group = group;
        this.title = title;
        this.description = description;
        this.meetingType = meetingType;
        this.maxMember = maxMember;
    }

    public static Event createEvent(Group group, String title, String description,
                                    MeetingType meetingType, Integer maxMember) {
        return new Event(group, title, description, meetingType, maxMember);
    }

    private static void validateForCreation(String title, MeetingType meetingType, Integer maxMember) {
        validateTitle(title);
        validateMeetingType(meetingType);
        validateMaxMember(maxMember);
    }

    private static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("이벤트 제목은 필수입니다.");
        }
        if (title.length() > 10) {
            throw new IllegalArgumentException("이벤트 제목은 10자를 초과할 수 없습니다.");
        }
    }

    private static void validateMeetingType(MeetingType meetingType) {
        if (meetingType == null) {
            throw new IllegalArgumentException("미팅 타입은 필수입니다.");
        }
    }

    private static void validateMaxMember(Integer maxMember) {
        if (maxMember == null || maxMember <= 0) {
            throw new IllegalArgumentException("최대 인원은 1명 이상이어야 합니다.");
        }
        if (maxMember > 100) {
            throw new IllegalArgumentException("최대 인원은 100명을 초과할 수 없습니다.");
        }
    }

    public void validateCapacity(Long currentMemberCount) {
        if (this.maxMember != null && currentMemberCount >= this.maxMember) {
            throw new InvalidEventDataException(EventErrorCode.EVENT_MEMBER_LIMIT_EXCEEDED);
        }
    }

}
