package com.grepp.spring.app.controller.api.schedules.payload.request;

import com.grepp.spring.app.model.schedule.code.Workspace;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWorkspaceRequest {
    private Workspace workspace;
    private String workspaceName;
    private String url;
}
