package com.grepp.spring.app.controller.api.group.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateGroupRequest {
    // TODO: groupName 중복 가능한지: 프론트와 협의
    // TODO: groupName, description 의 문자열 개수 제한: 프론트와 협의
    // TODO: description nullable: 프론트와 협의

    @NotBlank
    private String groupName;
    private String description;

}
