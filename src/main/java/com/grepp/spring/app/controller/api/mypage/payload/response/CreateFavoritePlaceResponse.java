package com.grepp.spring.app.controller.api.mypage.payload.response;

import com.grepp.spring.app.model.mypage.dto.FavoriteLocationDto;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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


