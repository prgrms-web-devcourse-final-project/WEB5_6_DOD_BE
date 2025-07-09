package com.grepp.spring.app.controller.api.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {

    private String id;
    private String role;
    private String name;
}
