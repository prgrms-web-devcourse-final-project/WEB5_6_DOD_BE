package com.grepp.spring.app.controller.api.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDeactivateResponse {

    private String id;
    private String role;
    private String name;

}
