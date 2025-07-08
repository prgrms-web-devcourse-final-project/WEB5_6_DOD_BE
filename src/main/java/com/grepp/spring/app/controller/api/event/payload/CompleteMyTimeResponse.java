package com.grepp.spring.app.controller.api.event.payload;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CompleteMyTimeResponse {
    private Long eventId;
    private Long eventMemberId;
    private Boolean isConfirmed;
    private LocalDateTime confirmedAt;
}