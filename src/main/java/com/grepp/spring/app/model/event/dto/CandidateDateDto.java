package com.grepp.spring.app.model.event.dto;

import com.grepp.spring.app.model.event.entity.CandidateDate;
import com.grepp.spring.app.model.event.entity.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CandidateDateDto {

    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public CandidateDate toEntity(Event event) {
        CandidateDate candidateDate = new CandidateDate();
        candidateDate.setEvent(event);
        candidateDate.setDate(this.date);
        candidateDate.setStartTime(this.startTime);
        candidateDate.setEndTime(this.endTime);
        return candidateDate;
    }

    public static List<CandidateDate> toEntityList(Event event, List<CandidateDateDto> dtoList) {
        return dtoList.stream()
            .map(dto -> dto.toEntity(event))
            .collect(Collectors.toList());
    }

}