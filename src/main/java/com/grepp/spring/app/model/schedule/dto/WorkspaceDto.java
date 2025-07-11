package com.grepp.spring.app.model.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WorkspaceDto {
    private String type;
    private String name;
    private String url;
}
