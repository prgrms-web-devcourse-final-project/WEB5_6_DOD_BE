package com.grepp.spring.app.controller.api.mypage.payload;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFavoritePlaceResponse {

  private List<FavoriteLocationList> fav_locations;

  @Getter
  @Setter
  public static class FavoriteLocationList{
    private Long favoritePlaceId;
    private String stationName;
    private double latitude;
    private double longitude;
    private LocalDateTime createdAt;
  }

}
