package com.grepp.spring.app.controller.api.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String id;
    private String role;
    private String name;
    private String accessToken;
    private String refreshToken;

}
