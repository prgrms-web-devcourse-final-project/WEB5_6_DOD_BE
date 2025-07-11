package com.grepp.spring.app.controller.api.mypage.payload.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowFavoritePlaceResponse {

  private List<ShowFavoritePlaceResponse.FavoriteLocationList> fav_locations;

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
