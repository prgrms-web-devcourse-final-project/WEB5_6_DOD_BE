package com.grepp.spring.app.model.event.dto;

import com.grepp.spring.app.controller.api.event.payload.response.AllTimeScheduleResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
public class AllTimeScheduleDto {
    private final Long eventId;
    private final String eventTitle;
    private final String description;
    private final TimeTableDto timeTable;
    private final List<MemberScheduleDto> memberSchedules;
    private final Integer maxMembers;
    private final Integer totalMembers;
    private final Integer confirmedMembers;
    private final Map<String, List<Integer>> participantCounts;

    @Getter
    @Builder
    public static class TimeTableDto {
        private final List<DateInfoDto> dates;
        private final String startTime;
        private final String endTime;
    }

    @Getter
    @Builder
    public static class DateInfoDto {
        private final String date;
        private final String dayOfWeek;
        private final String displayDate;
    }

    @Getter
    @Builder
    public static class MemberScheduleDto {
        private final String eventMemberId;
        private final String memberName;
        private final List<DailyTimeSlotDto> dailyTimeSlots;
        private final Boolean isConfirmed;
    }

    @Getter
    @Builder
    public static class DailyTimeSlotDto {
        private final String date;
        private final String timeBit;
    }

    public static AllTimeScheduleResponse fromDto(AllTimeScheduleDto dto) {
        AllTimeScheduleResponse.TimeTable timeTable = null;
        if (dto.getTimeTable() != null) {
            List<AllTimeScheduleResponse.DateInfo> dateInfos = dto.getTimeTable().getDates().stream()
                .map(AllTimeScheduleDto::convertDateInfo)
                .collect(Collectors.toList());
            timeTable = AllTimeScheduleResponse.TimeTable.builder()
                .dates(dateInfos)
                .startTime(dto.getTimeTable().getStartTime())
                .endTime(dto.getTimeTable().getEndTime())
                .build();
        }

        List<AllTimeScheduleResponse.MemberSchedule> memberSchedules = null;
        if (dto.getMemberSchedules() != null) {
            memberSchedules = dto.getMemberSchedules().stream()
                .map(AllTimeScheduleDto::convertMemberSchedule)
                .collect(Collectors.toList());
        }

        return AllTimeScheduleResponse.builder()
            .eventId(dto.getEventId())
            .eventTitle(dto.getEventTitle())
            .description(dto.getDescription())
            .timeTable(timeTable)
            .memberSchedules(memberSchedules)
            .maxMembers(dto.getMaxMembers())
            .totalMembers(dto.getTotalMembers())
            .confirmedMembers(dto.getConfirmedMembers())
            .participantCounts(dto.getParticipantCounts())
            .build();
    }

    private static AllTimeScheduleResponse.DateInfo convertDateInfo(DateInfoDto dto) {
        return AllTimeScheduleResponse.DateInfo.builder()
            .date(dto.getDate())
            .dayOfWeek(dto.getDayOfWeek())
            .displayDate(dto.getDisplayDate())
            .build();
    }

    private static AllTimeScheduleResponse.MemberSchedule convertMemberSchedule(MemberScheduleDto dto) {
        List<AllTimeScheduleResponse.DailyTimeSlot> dailyTimeSlots = null;
        if (dto.getDailyTimeSlots() != null) {
            dailyTimeSlots = dto.getDailyTimeSlots().stream()
                .map(AllTimeScheduleDto::convertDailyTimeSlot)
                .collect(Collectors.toList());
        }
        return AllTimeScheduleResponse.MemberSchedule.builder()
            .eventMemberId(dto.getEventMemberId())
            .memberName(dto.getMemberName())
            .dailyTimeSlots(dailyTimeSlots)
            .isConfirmed(dto.getIsConfirmed())
            .build();
    }

    private static AllTimeScheduleResponse.DailyTimeSlot convertDailyTimeSlot(DailyTimeSlotDto dto) {
        return AllTimeScheduleResponse.DailyTimeSlot.builder()
            .date(dto.getDate())
            .timeBit(dto.getTimeBit())
            .build();
    }

    public static String formatTimeBit(Long timeBit) {
        if (timeBit == null || timeBit == 0) {
            return "000000000000";
        }
        return String.format("%012X", timeBit);
    }

    public static String formatDisplayDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MM/dd"));
    }

    public static String formatDayOfWeek(LocalDate date) {
        return date.getDayOfWeek().toString().substring(0, 3).toUpperCase();
    }
}