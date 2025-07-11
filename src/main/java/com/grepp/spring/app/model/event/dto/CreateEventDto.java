package com.grepp.spring.app.model.event.dto;

import com.grepp.spring.app.controller.api.event.payload.request.CreateEventRequest;
import com.grepp.spring.app.model.event.code.MeetingType;
import com.grepp.spring.app.model.event.entity.Event;
import com.grepp.spring.app.model.group.entity.Group;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CreateEventDto {

    private final String title;
    private final String description;
    private final MeetingType meetingType;
    private final Integer maxMember;
    private final Long groupId;
    private final List<CandidateDateDto> candidateDates;
    private final String currentMemberId;

    public boolean isValid() {
        return title != null && !title.trim().isEmpty()
            && meetingType != null
            && maxMember != null && maxMember > 0
            && candidateDates != null && !candidateDates.isEmpty();
    }

    public static CreateEventDto toDto(CreateEventRequest webRequest, String currentMemberId) {
        return CreateEventDto.builder()
            .title(webRequest.getTitle())
            .description(webRequest.getDescription())
            .meetingType(MeetingType.valueOf(webRequest.getMeetingType()))
            .maxMember(webRequest.getMaxMember())
            .groupId(webRequest.getGroupId())
            .candidateDates(convertCandidateDates(webRequest.getDateList()))
            .currentMemberId(currentMemberId)
            .build();
    }

    private static List<CandidateDateDto> convertCandidateDates(List<CreateEventRequest.CandidateDateWeb> webCandidates) {
        return webCandidates.stream()
            .flatMap(webCandidate -> webCandidate.getDates().stream()
                .map(date -> CandidateDateDto.builder()
                    .date(date)
                    .startTime(webCandidate.getStartTime())
                    .endTime(webCandidate.getEndTime())
                    .build()))
            .collect(Collectors.toList());
    }

    public static Event toEntity(CreateEventDto dto, Group group) {
        return Event.createEvent(
            group,
            dto.getTitle(),
            dto.getDescription(),
            dto.getMeetingType(),
            dto.getMaxMember()
        );
    }

    public static Event toEntity(CreateEventDto dto) {
        return toEntity(dto, null);
    }

}