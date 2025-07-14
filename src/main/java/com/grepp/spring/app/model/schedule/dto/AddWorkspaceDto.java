package com.grepp.spring.app.model.schedule.dto;

import com.grepp.spring.app.controller.api.schedules.payload.request.AddWorkspaceRequest;
import com.grepp.spring.app.model.schedule.code.WorkspaceType;
import com.grepp.spring.app.model.schedule.entity.Schedule;
import com.grepp.spring.app.model.schedule.entity.Workspace;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddWorkspaceDto {
    private Schedule scheduleId;
    private WorkspaceType workspaceType;
    private String workspaceName;
    private String url;

    public static AddWorkspaceDto toDto(Optional<Schedule> ScheduleId, AddWorkspaceRequest request) {
        return AddWorkspaceDto.builder()
            .scheduleId(ScheduleId.orElse(null))
            .workspaceType(request.getWorkspaceType())
            .workspaceName(request.getWorkspaceName())
            .url(request.getUrl())
            .build();
    }

    public static Workspace fromDto(AddWorkspaceDto dto) {
        return Workspace.builder()
            .schedule(dto.getScheduleId())
            .type(dto.getWorkspaceType())
            .name(dto.getWorkspaceName())
            .url(dto.getUrl())
            .build();
    }
}
