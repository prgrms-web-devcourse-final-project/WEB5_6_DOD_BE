package com.grepp.spring.app.controller.api.event.payload;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinEventResponse {
    private String role;
    private LocalDateTime joinedAt;
}