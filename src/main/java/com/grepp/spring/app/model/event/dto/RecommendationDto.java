package com.grepp.spring.app.model.event.dto;

import com.grepp.spring.app.model.event.entity.EventMember;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public class RecommendationDto {

    @Getter
    @Builder
    public static class TimeSlot {
        private final LocalDate date;
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Set<String> availableMembers;
        private final Set<EventMember> availableEventMembers;
        private final Integer participantCount;
    }

    @Getter
    @Builder
    public static class ContinuousTimeRange {
        private final LocalDate date;
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final Set<String> availableMembers;
        private final Set<EventMember> availableEventMembers;
        private final Integer participantCount;
        private final Long durationMinutes;
    }

    @Getter
    @Builder
    public static class RecommendationInfo {
        private final LocalDateTime startDateTime;
        private final LocalDateTime endDateTime;
        private final Integer participantCount;
        private final Set<String> availableMembers;
        private final Set<EventMember> availableEventMembers;
        private final String type;
    }
}