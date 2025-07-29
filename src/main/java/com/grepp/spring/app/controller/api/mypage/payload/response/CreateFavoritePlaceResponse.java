package com.grepp.spring.app.controller.api.mypage.payload.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFavoritePlaceResponse {

  private List<FavoriteLocationList> fav_locations;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class FavoriteLocationList{
    private Long favoritePlaceId;
    private String stationName;
    private String address;
    private double latitude;
    private double longitude;
    private LocalDateTime createdAt;
  }


}


