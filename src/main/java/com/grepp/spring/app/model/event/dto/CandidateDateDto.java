package com.grepp.spring.app.model.event.dto;

import com.grepp.spring.app.model.event.entity.CandidateDate;
import com.grepp.spring.app.model.event.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class CandidateDateDto {

    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public static CandidateDate toEntity(CandidateDateDto dto, Event event) {
        CandidateDate candidateDate = new CandidateDate();
        candidateDate.setEvent(event);
        candidateDate.setDate(dto.getDate());
        candidateDate.setStartTime(dto.getStartTime());
        candidateDate.setEndTime(dto.getEndTime());
        return candidateDate;
    }

    public static List<CandidateDate> toEntityList(List<CandidateDateDto> dtos, Event event) {
        return dtos.stream()
            .map(dto -> toEntity(dto, event))
            .collect(Collectors.toList());
    }

}