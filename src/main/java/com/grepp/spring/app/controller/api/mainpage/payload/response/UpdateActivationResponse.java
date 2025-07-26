package com.grepp.spring.app.controller.api.mainpage.payload.response;

import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateActivationResponse {
  private Long scheduleMemberId;
  private boolean activated;

  public static UpdateActivationResponse from(ScheduleMember sm) {
    return new UpdateActivationResponse(sm.getId(), sm.getActivated());
  }

}
