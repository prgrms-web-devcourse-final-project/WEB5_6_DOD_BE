package com.grepp.spring.app.controller.api.mypage.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ModifyFavoritePlaceRequest {

  @NotNull
  @Schema(example = "10033")
  private Long favoritePlaceId;
  @NotNull
  @Schema(example = "합정역 8번 출구")
  private String stationName;
  @Schema(example = "서울특별시 마포구 양화로 72")
  @NotNull
  private String address;
  @Schema(example = "37.5492")
  @NotNull
  private double latitude;
  @Schema(example = "126.9135")
  @NotNull
  private double longitude;
}
