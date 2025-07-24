package com.grepp.spring.app.controller.api.mypage.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ModifyFavoritePlaceRequest {

  @Schema(example = "10033")
  private Long favoritePlaceId;
  @Schema(example = "합정역 8번 출구")
  private String stationName;
  @Schema(example = "37.5492")
  private double latitude;
  @Schema(example = "126.9135")
  private double longitude;

}
