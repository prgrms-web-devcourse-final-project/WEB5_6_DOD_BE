package com.grepp.spring.app.model.schedule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ZoomMeetingDto {
    @JsonProperty("join_url")
    private String joinUrl;
}
