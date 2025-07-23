package com.grepp.spring.app.model.event.dto;

import com.grepp.spring.app.controller.api.event.payload.response.ScheduleResultResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ScheduleResultDto {
    private final String eventTitle;
    private final Integer totalParticipants;
    private final RecommendationSummaryDto recommendation;

    @Getter
    @Builder
    public static class RecommendationSummaryDto {
        private final List<TimeSlotDetailDto> longestMeetingTimes;
        private final List<TimeSlotDetailDto> earliestMeetingTimes;
    }

    @Getter
    @Builder
    public static class TimeSlotDetailDto {
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;
        private final Integer participantCount;
        private final List<ParticipantDto> participants;
        private final Boolean isSelected;
        private final String timeSlotId;
    }

    @Getter
    @Builder
    public static class ParticipantDto {
        private final String memberId;
        private final String memberName;
    }

    public static ScheduleResultResponse fromDto(ScheduleResultDto dto) {
        ScheduleResultResponse.Recommendation recommendation = null;
        if (dto.getRecommendation() != null) {
            List<ScheduleResultResponse.TimeSlotDetail> longestTimes = dto.getRecommendation().getLongestMeetingTimes().stream()
                .map(ScheduleResultDto::convertToTimeSlotDetail)
                .collect(Collectors.toList());
            List<ScheduleResultResponse.TimeSlotDetail> earliestTimes = dto.getRecommendation().getEarliestMeetingTimes().stream()
                .map(ScheduleResultDto::convertToTimeSlotDetail)
                .collect(Collectors.toList());
            recommendation = ScheduleResultResponse.Recommendation.builder()
                .longestMeetingTimes(longestTimes)
                .earliestMeetingTimes(earliestTimes)
                .build();
        }

        return ScheduleResultResponse.builder()
            .eventTitle(dto.getEventTitle())
            .totalParticipants(dto.getTotalParticipants())
            .recommendation(recommendation)
            .build();
    }

    private static ScheduleResultResponse.TimeSlotDetail convertToTimeSlotDetail(TimeSlotDetailDto dto) {
        List<ScheduleResultResponse.Participant> participants = null;
        if (dto.getParticipants() != null) {
            participants = dto.getParticipants().stream()
                .map(ScheduleResultDto::convertToParticipant)
                .collect(Collectors.toList());
        }

        return ScheduleResultResponse.TimeSlotDetail.builder()
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .participantCount(dto.getParticipantCount())
            .participants(participants)
            .isSelected(dto.getIsSelected())
            .timeSlotId(dto.getTimeSlotId())
            .build();
    }

    private static ScheduleResultResponse.Participant convertToParticipant(ParticipantDto dto) {
        return ScheduleResultResponse.Participant.builder()
            .memberId(dto.getMemberId())
            .memberName(dto.getMemberName())
            .build();
    }

}