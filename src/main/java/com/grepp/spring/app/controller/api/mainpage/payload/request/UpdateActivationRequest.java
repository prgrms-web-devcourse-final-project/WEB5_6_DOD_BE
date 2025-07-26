package com.grepp.spring.app.controller.api.mainpage.payload.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateActivationRequest {
  private boolean activated;

}
