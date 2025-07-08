package com.grepp.spring.app.controller.api.auth.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {

    @Schema(example = "GOOGLE_1234")
    private String id;
    @Schema(example = "123qwe!@#")
    private String password;
}
