package com.grepp.spring.app.controller.api.mypage.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyFavoritePlaceRequest {
  private Long favoritePlaceId;
  private String stationName;
  private double latitude;
  private double longitude;

}
