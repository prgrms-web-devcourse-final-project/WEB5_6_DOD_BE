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
        ScheduleResultResponse response = new ScheduleResultResponse();

        response.setEventTitle(dto.getEventTitle());
        response.setTotalParticipants(dto.getTotalParticipants());

        if (dto.getRecommendation() != null) {
            ScheduleResultResponse.Recommendation recommendation = new ScheduleResultResponse.Recommendation();

            if (dto.getRecommendation().getLongestMeetingTimes() != null) {
                List<ScheduleResultResponse.TimeSlotDetail> longestTimes = dto.getRecommendation().getLongestMeetingTimes().stream()
                    .map(ScheduleResultDto::convertToTimeSlotDetail)
                    .collect(Collectors.toList());
                recommendation.setLongestMeetingTimes(longestTimes);
            }

            if (dto.getRecommendation().getEarliestMeetingTimes() != null) {
                List<ScheduleResultResponse.TimeSlotDetail> earliestTimes = dto.getRecommendation().getEarliestMeetingTimes().stream()
                    .map(ScheduleResultDto::convertToTimeSlotDetail)
                    .collect(Collectors.toList());
                recommendation.setEarliestMeetingTimes(earliestTimes);
            }

            response.setRecommendation(recommendation);
        }

        return response;
    }

    private static ScheduleResultResponse.TimeSlotDetail convertToTimeSlotDetail(TimeSlotDetailDto dto) {
        ScheduleResultResponse.TimeSlotDetail timeSlotDetail = new ScheduleResultResponse.TimeSlotDetail();

        timeSlotDetail.setStartTime(dto.getStartTime());
        timeSlotDetail.setEndTime(dto.getEndTime());
        timeSlotDetail.setParticipantCount(dto.getParticipantCount());
        timeSlotDetail.setIsSelected(dto.getIsSelected());
        timeSlotDetail.setTimeSlotId(dto.getTimeSlotId());

        if (dto.getParticipants() != null) {
            List<ScheduleResultResponse.Participant> participants = dto.getParticipants().stream()
                .map(ScheduleResultDto::convertToParticipant)
                .collect(Collectors.toList());
            timeSlotDetail.setParticipants(participants);
        }

        return timeSlotDetail;
    }

    private static ScheduleResultResponse.Participant convertToParticipant(ParticipantDto dto) {
        ScheduleResultResponse.Participant participant = new ScheduleResultResponse.Participant();
        participant.setMemberId(dto.getMemberId());
        participant.setMemberName(dto.getMemberName());
        return participant;
    }

}