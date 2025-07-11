package com.grepp.spring.app.controller.api.event.payload.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteEventResponse {
    private Long eventId;
    private LocalDateTime deletedAt;
}
