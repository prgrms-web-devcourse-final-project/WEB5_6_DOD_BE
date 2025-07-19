package com.grepp.spring.app.controller.api.schedule.payload.request;

import com.grepp.spring.app.model.schedule.code.WorkspaceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddWorkspaceRequest {
    private WorkspaceType workspaceType;
    private String workspaceName;
    private String url;
}
