package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.model.schedule.code.WorkspaceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ModifyWorkspaceDto {
    private WorkspaceType type;
    private String name;
    private String url;
}
