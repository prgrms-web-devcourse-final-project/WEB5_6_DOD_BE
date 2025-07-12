package com.grepp.spring.app.model.event.dto;

import com.grepp.spring.app.controller.api.event.payload.request.MyTimeScheduleRequest;
import com.grepp.spring.app.model.event.entity.EventMember;
import com.grepp.spring.app.model.event.entity.TempSchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MyTimeScheduleDto {
    private final Long eventId;
    private final String memberId;
    private final String timezone;
    private final List<DailyTimeSlotDto> dailyTimeSlots;

    @Getter
    @Builder
    public static class DailyTimeSlotDto {
        private final LocalDate date;
        private final String timeBit; // 16진수 12자리

        public Long getTimeBitAsLong() {
            return Long.parseUnsignedLong(this.timeBit, 16);
        }

        public static TempSchedule toEntity(DailyTimeSlotDto dto, EventMember eventMember) {
            TempSchedule tempSchedule = new TempSchedule();
            tempSchedule.setEventMember(eventMember);
            tempSchedule.setDate(dto.getDate());
            tempSchedule.setTimeBit(dto.getTimeBitAsLong());
            return tempSchedule;
        }
    }

    public static MyTimeScheduleDto toDto(MyTimeScheduleRequest request, Long eventId, String memberId) {
        List<DailyTimeSlotDto> dailyTimeSlots = request.getDailyTimeSlots().stream()
            .map(slot -> DailyTimeSlotDto.builder()
                .date(slot.getDate())
                .timeBit(slot.getTimeBit().toUpperCase())
                .build())
            .collect(Collectors.toList());

        return MyTimeScheduleDto.builder()
            .eventId(eventId)
            .memberId(memberId)
            .timezone(request.getTimezone())
            .dailyTimeSlots(dailyTimeSlots)
            .build();
    }

    public static List<TempSchedule> toEntityList(MyTimeScheduleDto dto, EventMember eventMember) {
        return dto.getDailyTimeSlots().stream()
            .map(slot -> DailyTimeSlotDto.toEntity(slot, eventMember))
            .collect(Collectors.toList());
    }
}