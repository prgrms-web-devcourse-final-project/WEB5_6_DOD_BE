package com.grepp.spring.app.controller.api.event.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventResponse {
    private Long eventId;
    private String title;
    private String type;
    private String shareLink;
    private LocalDateTime createdAt;
}
