package com.grepp.spring.app.controller.api.auth.payload;

import lombok.Data;

@Data
public class RegisterRequest {

    private String id;
    private String password;
    private String provider;
    private String role;
    private String email;
    private String name;
    private Long profileImageNumber;
    private String tel;

}
