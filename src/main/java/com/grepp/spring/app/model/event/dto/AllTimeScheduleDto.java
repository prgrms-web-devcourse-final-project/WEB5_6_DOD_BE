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
        AllTimeScheduleResponse response = new AllTimeScheduleResponse();

        response.setEventId(dto.getEventId());
        response.setEventTitle(dto.getEventTitle());
        response.setDescription(dto.getDescription());
        response.setTotalMembers(dto.getTotalMembers());
        response.setConfirmedMembers(dto.getConfirmedMembers());
        response.setParticipantCounts(dto.getParticipantCounts());

        if (dto.getTimeTable() != null) {
            AllTimeScheduleResponse.TimeTable timeTable = new AllTimeScheduleResponse.TimeTable();

            if (dto.getTimeTable().getDates() != null) {
                List<AllTimeScheduleResponse.DateInfo> dateInfos = dto.getTimeTable().getDates().stream()
                    .map(AllTimeScheduleDto::convertDateInfo)
                    .collect(Collectors.toList());
                timeTable.setDates(dateInfos);
            }

            timeTable.setStartTime(dto.getTimeTable().getStartTime());
            timeTable.setEndTime(dto.getTimeTable().getEndTime());
            response.setTimeTable(timeTable);
        }

        if (dto.getMemberSchedules() != null) {
            List<AllTimeScheduleResponse.MemberSchedule> memberSchedules = dto.getMemberSchedules().stream()
                .map(AllTimeScheduleDto::convertMemberSchedule)
                .collect(Collectors.toList());
            response.setMemberSchedules(memberSchedules);
        }

        return response;
    }

    private static AllTimeScheduleResponse.DateInfo convertDateInfo(DateInfoDto dto) {
        AllTimeScheduleResponse.DateInfo dateInfo = new AllTimeScheduleResponse.DateInfo();
        dateInfo.setDate(dto.getDate());
        dateInfo.setDayOfWeek(dto.getDayOfWeek());
        dateInfo.setDisplayDate(dto.getDisplayDate());
        return dateInfo;
    }

    private static AllTimeScheduleResponse.MemberSchedule convertMemberSchedule(MemberScheduleDto dto) {
        AllTimeScheduleResponse.MemberSchedule memberSchedule = new AllTimeScheduleResponse.MemberSchedule();

        memberSchedule.setEventMemberId(dto.getEventMemberId());
        memberSchedule.setMemberName(dto.getMemberName());
        memberSchedule.setIsConfirmed(dto.getIsConfirmed());

        if (dto.getDailyTimeSlots() != null) {
            List<AllTimeScheduleResponse.DailyTimeSlot> dailyTimeSlots = dto.getDailyTimeSlots().stream()
                .map(AllTimeScheduleDto::convertDailyTimeSlot)
                .collect(Collectors.toList());
            memberSchedule.setDailyTimeSlots(dailyTimeSlots);
        }

        return memberSchedule;
    }

    private static AllTimeScheduleResponse.DailyTimeSlot convertDailyTimeSlot(DailyTimeSlotDto dto) {
        AllTimeScheduleResponse.DailyTimeSlot dailyTimeSlot = new AllTimeScheduleResponse.DailyTimeSlot();
        dailyTimeSlot.setDate(dto.getDate());
        dailyTimeSlot.setTimeBit(dto.getTimeBit());
        return dailyTimeSlot;
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